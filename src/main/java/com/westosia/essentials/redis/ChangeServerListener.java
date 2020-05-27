package com.westosia.essentials.redis;

import com.westosia.redisapi.redis.RedisChannelListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChangeServerListener implements RedisChannelListener {

    private static List<String> changing = new ArrayList<>();

    @Override
    public void messageReceived(String message) {
        changing.remove(message);
    }

    public static boolean isChanging(UUID uuid) {
        return changing.contains(uuid.toString());
    }

    public static void setChanging(UUID uuid, boolean isChanging) {
        if (isChanging) {
            changing.add(uuid.toString());
        } else {
            changing.remove(uuid.toString());
        }
    }
}
