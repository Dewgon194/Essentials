package com.westosia.essentials.redis;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.redisapi.redis.RedisChannelListener;

import java.util.Collection;
import java.util.UUID;

public class QueryHomesListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        String[] split = message.split("\\|");
        String server = split[0];
        if (Main.getInstance().SERVER_NAME.equals(server)) {
            // Load homes based on this server's cached homes
            UUID uuid = UUID.fromString(split[1]);
            Collection<Home> homes = HomeManager.getHomes(uuid).values();
            if (!homes.isEmpty()) {
                homes.forEach((home) -> {
                    RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_HOME, home.toString());
                });
            } else {
                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_HOME, split[1]);
            }
        }
    }
}
