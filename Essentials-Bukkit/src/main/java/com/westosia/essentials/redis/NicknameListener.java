package com.westosia.essentials.redis;

import com.westosia.essentials.bukkit.Main;
import com.westosia.redisapi.redis.RedisChannelListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NicknameListener implements RedisChannelListener {

    @Override
    public void messageReceived(String nick) {
        String[] split = nick.split("\\|");
        String username = split[0];
        String nickname = split[1];
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            Player player = Bukkit.getPlayer(username);
            if (player != null){
                player.setDisplayName(nickname);
            }
        });



    }

}
