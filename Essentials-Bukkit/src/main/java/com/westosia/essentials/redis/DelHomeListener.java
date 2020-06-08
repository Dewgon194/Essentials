package com.westosia.essentials.redis;

import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.redisapi.redis.RedisChannelListener;

import java.util.UUID;

public class DelHomeListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        // Uncache a home
        if (message.contains("|")) {
            Home home = HomeManager.fromString(message);
            HomeManager.removeHome(home);
        } else {
            // Uncache a user/all their homes
            HomeManager.removePlayer(UUID.fromString(message));
        }
    }
}
