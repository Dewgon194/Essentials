package com.westosia.essentials.redis;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.utils.ServerChangeHelper;
import com.westosia.redisapi.redis.RedisChannelListener;
import com.westosia.redisapi.redis.RedisConnector;
import org.bukkit.Bukkit;

import java.util.*;

public class ChangeServerListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        if (Main.getInstance().isEnabled()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                // Player has not connected to another server in 5 seconds, assume them offline
                if (ServerChangeInfo.isChangingServers(message)) {
                    Collection<Home> homes = new ArrayList<>(HomeManager.getHomes(Bukkit.getOfflinePlayer(UUID.fromString(message))).values());
                    // Already saved to database, just uncache locally
                    if (ServerChangeInfo.savedToDbOnChange(message)) {
                        homes.forEach(HomeManager::removeHome);
                    } else {
                        // Not saved to database; ask server the player came from to save to database
                        if (Main.getInstance().serverName.equalsIgnoreCase(ServerChangeInfo.disconnectedFrom(message))) {
                            ServerChangeHelper.saveHomesToDB(message);
                            // Ask other servers to uncache homes
                            homes.forEach((home) -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().DEL_HOME_REDIS_CHANNEL, home.toString()));
                        }
                    }
                }
            }, 100);
        }
    }
}
