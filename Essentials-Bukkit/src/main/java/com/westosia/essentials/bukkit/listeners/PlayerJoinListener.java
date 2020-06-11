package com.westosia.essentials.bukkit.listeners;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Logger;
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

        UUID uuid = event.getPlayer().getUniqueId();
        // Wait a moment because this event fires before the Redis event from leaving
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                // Tell Redis that this player joined a server, so that they don't get marked as logged off the network
                // and their homes uncached
                if (ServerChange.isChangingServers(uuid)) {
                    ServerChange serverChange = ServerChange.getServerChange(uuid);
                    if (serverChange.getToServer().isEmpty()) {
                        serverChange.setToServer(Main.getInstance().serverName);
                    }
                    serverChange.setComplete(true);
                    RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.toString());
                }
            }
        }, 10);
        // Tell Redis that this player joined a server, so that they don't get marked as logged off the network
        // and their homes uncached
        /*
        UUID uuid = event.getPlayer().getUniqueId();
        if (ServerChange.isChangingServers(uuid)) {
            Logger.info("changing servers");
            ServerChange serverChange = ServerChange.getServerChange(uuid);
            if (serverChange.getToServer().isEmpty()) {
                Logger.info("declaring target server");
                serverChange.setToServer(Main.getInstance().serverName);
            }
            serverChange.setComplete(true);
            RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.toString());
        }*/
        //Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> RedisConnector.getInstance().getConnection().set("homes." + uuid.toString() + ".changing-servers", "false"));
        if (!DatabaseEditor.getNick(uuid).equals("")) {
            event.getPlayer().setDisplayName(DatabaseEditor.getNick(uuid));
        }
    }
}
