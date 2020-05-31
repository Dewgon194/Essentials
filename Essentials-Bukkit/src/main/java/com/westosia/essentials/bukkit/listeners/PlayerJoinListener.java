package com.westosia.essentials.bukkit.listeners;

import com.westosia.essentials.bukkit.Main;
import com.westosia.redisapi.redis.RedisConnector;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Get server name if it didn't get it on enable
        if (Main.getInstance().serverName.isEmpty()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> Main.getInstance().queryServerName());
        }
        // Tell Redis that this player joined a server, so that they don't get marked as logged off the network
        // and their homes uncached
        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> RedisConnector.getInstance().getConnection().set("homes." + uuid.toString() + ".changing-servers", "false"));
    }
}
