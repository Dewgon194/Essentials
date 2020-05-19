package com.westosia.essentials.redis;

import com.westosia.essentials.core.homes.Home;
import com.westosia.essentials.core.homes.HomeManager;
import com.westosia.redisapi.redis.RedisChannelListener;
import org.bukkit.Bukkit;

public class SetHomeListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        Home home = HomeManager.fromString(message);
        HomeManager.cacheHome(home);
        Bukkit.broadcastMessage(message);
    }
}
