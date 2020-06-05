package com.westosia.essentials.redis;

import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.redisapi.redis.RedisChannelListener;
import com.westosia.westosiaapi.utils.Logger;

public class DelHomeListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        Home home = HomeManager.fromString(message);
        HomeManager.removeHome(home);
    }
}
