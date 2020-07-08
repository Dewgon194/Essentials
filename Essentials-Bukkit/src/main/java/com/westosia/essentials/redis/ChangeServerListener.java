package com.westosia.essentials.redis;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.redisapi.redis.RedisChannelListener;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class ChangeServerListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        // Did not pass a server change, should be a UUID, which means uncache
        if (!message.contains("|")) {
            ServerChange serverChange = ServerChange.getServerChange(UUID.fromString(message));
            if (serverChange != null) {
                serverChange.uncache();
            }
        } else {
            ServerChange serverChange = ServerChange.fromString(message);
            serverChange.cache();
            if (Main.getInstance().isEnabled()) {
                // This is for a start server change
                if (ServerChange.isChangingServers(serverChange.getWhosChanging())) {
                    // Player was found on another server after a shutdown -- mark their change complete
                    // (And make sure to notify everyone else!)
                    if (serverChange.getReason() == ServerChange.Reason.SERVER_DOWN) {
                        if (Bukkit.getPlayer(serverChange.getWhosChanging()) != null) {
                            serverChange.setToServer(Main.getInstance().SERVER_NAME);
                            serverChange.setComplete(true);
                            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> RedisConnector.getInstance().getConnection().publish(RedisAnnouncer.Channel.CHANGE_SERVER.getChannel(), serverChange.toString()), 2);
                        }
                    }
                    // Only do this on one server
                    if (serverChange.getFromServer().equalsIgnoreCase(Main.getInstance().SERVER_NAME)) {
                        // Start a timer for 5 seconds, if the ServerChange hasn't updated, they are offline
                        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                            // Player has not connected to another server in 5 seconds, assume them offline
                            if (ServerChange.isChangingServers(serverChange.getWhosChanging())) {
                                // Already saved to database, just uncache locally
                                if (serverChange.getReason() != ServerChange.Reason.SERVER_DOWN) {
                                    // Not saved to database; ask server the player came from to save to database
                                    DatabaseEditor.saveAllHomes(serverChange.getWhosChanging());
                                }
                                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.DEL_HOME, serverChange.getWhosChanging().toString());
                                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.getWhosChanging().toString());
                                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_BACKHOME, serverChange.getWhosChanging().toString());

                                Jedis jedis = RedisConnector.getInstance().getConnection();
                                jedis.del(serverChange.getWhosChanging() + ".back");
                                Logger.info("deleted " + serverChange.getWhosChanging() + " backhomes");
                            }
                        }, 100);
                    }
                } else {
                    // This is for an end server change
                    serverChange.uncache();
                }
            }
        }
    }
}
