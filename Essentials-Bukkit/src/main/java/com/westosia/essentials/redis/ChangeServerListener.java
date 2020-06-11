package com.westosia.essentials.redis;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.redisapi.redis.RedisChannelListener;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;

public class ChangeServerListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        ServerChange serverChange = ServerChange.fromString(message);
        serverChange.cache();
        Logger.info(serverChange.toString());
        if (Main.getInstance().isEnabled()) {
            // This is for a start server change
            if (ServerChange.isChangingServers(serverChange.getWhosChanging())) {
                if (serverChange.getFromServer().equalsIgnoreCase(Main.getInstance().serverName)) {
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
