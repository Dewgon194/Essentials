package com.westosia.essentials.redis;

import com.westosia.essentials.bukkit.Main;
import com.westosia.redisapi.redis.RedisChannelListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SudoListener implements RedisChannelListener {

    @Override
    public void messageReceived(String message) {
        String[] split = message.split("\\|");
        UUID uuid = UUID.fromString(split[0]);
        String cmd = split[1];
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (cmd.startsWith("c:")) {
                    String chat = cmd.substring(2);
                    player.chat(chat);
                } else {
                    player.performCommand(cmd);
                }
            }
        });
    }
}
