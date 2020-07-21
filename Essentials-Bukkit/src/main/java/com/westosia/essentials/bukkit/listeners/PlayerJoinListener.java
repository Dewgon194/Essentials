package com.westosia.essentials.bukkit.listeners;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null); // Suppress Bukkit join message

        Player player = event.getPlayer();
        // If a newbie, send them to first spawn point
        if (!player.hasPlayedBefore()) {
            player.teleport(Main.getInstance().FIRST_SPAWN_LOC);
        } else {
            player.teleport(Main.getInstance().SPAWN_LOC);
        }

        // Get server name if it didn't get it on enable
        if (Main.getInstance().SERVER_NAME.isEmpty()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> Main.getInstance().queryServerName());
        }

        UUID uuid = player.getUniqueId();
        // Wait a moment because this event fires before the Redis event from leaving
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                // Tell Redis that this player joined a server, so that they don't get marked as logged off the network
                // and their homes uncached
                if (ServerChange.isChangingServers(uuid)) {
                    ServerChange serverChange = ServerChange.getServerChange(uuid);
                    if (serverChange.getToServer().isEmpty()) {
                        serverChange.setToServer(Main.getInstance().SERVER_NAME);
                    }
                    serverChange.setComplete(true);
                    RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.toString());
                    // If player has no homes here, the server restarted. Get them from the server they were just on
                    // (Or, if they came here because their previous server shut down, check the database)
                    if (HomeManager.getHomes(player) == null) {
                        // Joined because server went down, load from database
                        if (serverChange.getReason() == ServerChange.Reason.SERVER_DOWN) {
                            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                                Collection<Home> dbHomes = DatabaseEditor.getHomesInDB(uuid).values();
                                dbHomes.forEach(HomeManager::cacheHome);
                            });
                        } else {
                            // Joined for any other reason. Check previous server for homes
                            String fromServer = serverChange.getFromServer();
                            RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.QUERY_HOMES, fromServer + "|" + uuid.toString());
                        }
                    }
                    // Send player to home if that's the server change reason
                    if (serverChange.getReason() == ServerChange.Reason.HOME_TELEPORT) {
                        String homeString = serverChange.readInfo();
                        Home home = HomeManager.fromString(homeString);
                        player.teleport(home.getLocation());
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Teleported to home &f" + home.getName());
                    }
                } else {
                    // Just joined the server, load homes
                    if (HomeManager.getHomes(player) == null) {
                        Logger.info(uuid.toString()  + " needs homes loaded");
                        // Tell each server to load this player's homes via Redis
                        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                            // Get homes from database
                            Collection<Home> dbHomes = DatabaseEditor.getHomesInDB(uuid).values();
                            if (!dbHomes.isEmpty()) {
                                dbHomes.forEach(home -> RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_HOME, home.toString()));
                            } else {
                                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_HOME, uuid.toString());
                            }

                            // Update last seen to current time
                            DatabaseEditor.setLastSeen(uuid, Instant.now().getEpochSecond());
                        });
                    }
                }
            }
        }, 10);
        if (!DatabaseEditor.getNick(uuid).equals("")) {
            player.setDisplayName(DatabaseEditor.getNick(uuid));
        }

    }
}
