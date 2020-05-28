package com.westosia.essentials.utils;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.core.homes.Home;
import com.westosia.essentials.core.homes.HomeManager;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerChangeHelper {

    public static void saveHomesToDB(String uuidString) {
        // Player has left; send Redis message for all servers to unload and save to database
        Map<String, Home> currentHomes = new HashMap<>(HomeManager.getHomes(Bukkit.getOfflinePlayer(UUID.fromString(uuidString))));
        Logger.info("saving to database " + currentHomes.keySet().size() + " homes for uuid " + uuidString);
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
                //RedisConnector.getInstance().getConnection().publish(Main.getInstance().DEL_HOME_REDIS_CHANNEL, homeEntry.getValue().toString());
            }
        });
    }
}
