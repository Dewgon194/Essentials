package com.westosia.essentials.bukkit.listeners;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.core.homes.Home;
import com.westosia.essentials.core.homes.HomeManager;
import com.westosia.essentials.redis.ChangeServerListener;
import com.westosia.essentials.redis.ServerChangeInfo;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.ServerChangeHelper;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        // Tell Redis that this player is changing servers
        // If they join another server, Redis will report back and remove their changing status
        // If they don't, Redis will hear nothing. Check back in 5 seconds, if Redis hasn't removed changing status,
        // assume they are offline and uncache their homes
        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            ServerChangeInfo.tellRedis(uuid.toString(), "true", "cache");
        });
        //Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().CHANGE_SERVER_REDIS_CHANNEL, uuid.toString()), 100);
    }
}
