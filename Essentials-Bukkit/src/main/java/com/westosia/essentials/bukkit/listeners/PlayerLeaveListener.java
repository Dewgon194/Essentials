package com.westosia.essentials.bukkit.listeners;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerLeaveListener implements Listener {

    //TODO: fix this sometimes saving on leave when it shouldnt. may happen when there is one home in memory and none in db?
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        // Tell Redis that this player is changing servers
        // If they join another server, Redis will report back and remove their changing status
        // If they don't, Redis will hear nothing. Check back in 5 seconds, if Redis hasn't removed changing status,
        // assume they are offline and uncache their homes
        UUID uuid = event.getPlayer().getUniqueId();
        ServerChange serverChange = new ServerChange(uuid, ServerChange.Reason.VOLUNTARY, Main.getInstance().serverName);
        RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.toString());
    }
/*
    @EventHandler
    public void onKick(PlayerKickEvent event) {
        // Player was kicked due to server shutting down/restarting
        if (event.getReason().equals(Bukkit.getShutdownMessage())) {
            UUID uuid = event.getPlayer().getUniqueId();
            ServerChange serverChange = new ServerChange(uuid, ServerChange.Reason.SERVER_DOWN, Main.getInstance().serverName);
            RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.toString());
        }
    }*/
}
