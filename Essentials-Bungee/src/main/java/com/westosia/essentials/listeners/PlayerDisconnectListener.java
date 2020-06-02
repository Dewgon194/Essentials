package com.westosia.essentials.listeners;

import com.westosia.essentials.utils.TeleportRequest;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        TeleportRequest.removeRequest(player);
    }
}
