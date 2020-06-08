package com.westosia.essentials.redis;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.redisapi.redis.RedisChannelListener;
import com.westosia.redisapi.redis.RedisConnector;
import org.bukkit.Bukkit;

import java.util.*;

public class ChangeServerListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        ServerChange serverChange = ServerChange.fromString(message);
        serverChange.cache();
        // Testing branches
        if (Main.getInstance().isEnabled()) {
            // This is for a start server change
            if (ServerChange.isChangingServers(serverChange.getWhosChanging())) {
                // Start a timer for 5 seconds, if the ServerChange hasn't updated, they are offline
                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                    // Player has not connected to another server in 5 seconds, assume them offline
                    if (ServerChange.isChangingServers(serverChange.getWhosChanging())) {
                        //Collection<Home> homes = new ArrayList<>(HomeManager.getHomes(serverChange.getWhosChanging()).values());
                        // Already saved to database, just uncache locally
                        if (serverChange.getReason() != ServerChange.Reason.SERVER_DOWN) {
                            // Not saved to database; ask server the player came from to save to database
                            if (Main.getInstance().serverName.equalsIgnoreCase(serverChange.getFromServer())) {
                                DatabaseEditor.saveAllHomes(serverChange.getWhosChanging());
                                // Ask other servers to uncache homes
                                //homes.forEach((home) -> RedisConnector.getInstance().getConnection().publish(RedisAnnouncer.Channel.DEL_HOME.getChannel(), home.toString()));
                            }
                        }
                        HomeManager.removePlayer(serverChange.getWhosChanging());
                    }
                }, 100);
            }
        }
    }
}
