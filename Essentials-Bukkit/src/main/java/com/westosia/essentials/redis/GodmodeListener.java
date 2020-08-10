package com.westosia.essentials.redis;

import com.westosia.essentials.bukkit.Main;
import com.westosia.redisapi.redis.RedisChannelListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class GodmodeListener implements RedisChannelListener {

    @Override
    public void messageReceived(String godmode) {
        String[] split = godmode.split("\\|");
        String username = split[0];
        String onOff = split[0];
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            Player player = Bukkit.getPlayer(username);
            if (player != null) {
                if (onOff.equals("on")) {
                    if (!player.hasMetadata("GodmodeOn")){
                        player.setMetadata("GodmodeOn", new FixedMetadataValue(Main.getInstance(), "godmode"));
                    }
                }else if (onOff.equals("off")){
                    if (player.hasMetadata("GodmodeOn")){
                        player.removeMetadata("GodmodeOn", Main.getInstance());
                    }
                }
            }
        });

    }
}


