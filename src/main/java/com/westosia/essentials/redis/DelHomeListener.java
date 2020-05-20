package com.westosia.essentials.redis;

import com.westosia.essentials.core.homes.Home;
import com.westosia.essentials.core.homes.HomeManager;
import com.westosia.redisapi.redis.RedisChannelListener;

public class DelHomeListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        Home home = HomeManager.fromString(message);
        HomeManager.removeHome(home);
    }
}
