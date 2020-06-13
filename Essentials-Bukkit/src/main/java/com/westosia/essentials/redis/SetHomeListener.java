package com.westosia.essentials.redis;

import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.redisapi.redis.RedisChannelListener;

import java.util.UUID;

public class SetHomeListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        // It's a home, cache it
        if (message.contains("|")) {
            Home home = HomeManager.fromString(message);
            HomeManager.cacheHome(home);
        } else {
            // It's just a UUID, create an empty list (player has no homes)
            HomeManager.newEntry(UUID.fromString(message));
        }
    }
}
