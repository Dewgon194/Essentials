package com.westosia.essentials.bukkit.listeners;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.core.homes.Home;
import com.westosia.essentials.core.homes.HomeManager;
import com.westosia.essentials.redis.ChangeServerListener;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerLeaveListener implements Listener {

    public static Map<UUID, Integer> timeOuts = new HashMap<>();

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        // Tell Redis that this player is changing servers
        // If they join another server, Redis will report back and remove their changing status
        // If they don't, Redis will hear nothing. Check back in 5 seconds, if Redis hasn't removed changing status,
        // assume they are offline and uncache their homes
        UUID uuid = event.getPlayer().getUniqueId();
        ChangeServerListener.setChanging(uuid, true);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (ChangeServerListener.isChanging(uuid)) {
                    saveHomes(uuid.toString());
                    ChangeServerListener.setChanging(uuid, false);
                }
            }
        }, 100);
    }

    private void saveHomes(String uuidString) {
        // Player has left; send Redis message for all servers to unload and save to database
        Map<String, Home> currentHomes = new HashMap<>(HomeManager.getHomes(Bukkit.getOfflinePlayer(UUID.fromString(uuidString))));
        Logger.info("unloading " + currentHomes.keySet().size() + " homes for uuid " + uuidString);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            Map<String, Home> dbHomes = DatabaseEditor.getHomesInDB(UUID.fromString(uuidString));
            // Get all homes from database that aren't in the current list. These need to be deleted
            dbHomes.forEach((dbHomeName, dbHome) -> {
                // Database home does not exist in current homes. Delete from database
                if (!currentHomes.containsKey(dbHomeName)) {
                    DatabaseEditor.deleteHome(dbHome);
                }
            });

            // Determine if home needs to be updated or inserted into database
            for (Map.Entry<String, Home> homeEntry : currentHomes.entrySet()) {
                // Current home does not yet exist in database, insert it now
                boolean newEntry = false;
                if (dbHomes.isEmpty() || !dbHomes.containsKey(homeEntry.getKey())) {
                    newEntry = true;
                }
                DatabaseEditor.saveHome(homeEntry.getValue(), newEntry);
                // Tell Redis to delete all current homes from cache
                RedisConnector.getInstance().getConnection().publish(Main.getInstance().DEL_HOME_REDIS_CHANNEL, homeEntry.getValue().toString());
            }
        });
    }
}
