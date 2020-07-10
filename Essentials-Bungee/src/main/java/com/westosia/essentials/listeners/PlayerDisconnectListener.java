package com.westosia.essentials.listeners;

import com.westosia.essentials.utils.teleports.TeleportRequest;
import com.westosia.essentials.utils.Text;
import de.myzelyam.api.vanish.BungeeVanishAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (!BungeeVanishAPI.isInvisible(player)) { // ignore IntelliJ warning Vanish is weird and defaults to false
            ProxyServer.getInstance().broadcast(Text.format("&c&l- " + Text.getPrefix(player) + player.getName()));
        }

        TeleportRequest.removeRequest(player);
    }
}
