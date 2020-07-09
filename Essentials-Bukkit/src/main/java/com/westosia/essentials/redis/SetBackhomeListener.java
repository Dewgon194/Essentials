package com.westosia.essentials.redis;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.homes.back.BackManager;
import com.westosia.redisapi.redis.RedisChannelListener;

import java.util.UUID;

public class SetBackhomeListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        if (Main.getInstance().isEnabled()) {
            if (message.contains("|")) {
                // It's a backhome, cache it
                Home backhome = HomeManager.fromString(message);
                BackManager.setBackHome(backhome);
            } else {
                if (!message.contains(":")) {
                    // It's a UUID, remove cache
                    BackManager.removeEntry(UUID.fromString(message));
                } else {
                    String[] split = message.split(":");
                    UUID uuid = UUID.fromString(split[0]);
                    int index = Integer.parseInt(split[1]);
                    BackManager.cacheBackIndex(uuid, index);
                }
            }
        }
    }
}
