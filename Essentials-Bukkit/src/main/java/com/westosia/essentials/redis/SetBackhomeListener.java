package com.westosia.essentials.redis;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.homes.back.BackManager;
import com.westosia.redisapi.redis.RedisChannelListener;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

public class SetBackhomeListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        if (Main.getInstance().isEnabled()) {
            if (message.contains("|")) {
                // It's a backhome, cache it
                Home backhome = HomeManager.fromString(message);
                BackManager.setBackHome(backhome);
            } else {
                // It's a UUID, remove cache
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        Jedis jedis = RedisConnector.getInstance().getConnection();
                        jedis.del(message + ".back");
                        Logger.info("deleted " + message + " backhomes");
                    }
                });
            }
        }
    }
}
