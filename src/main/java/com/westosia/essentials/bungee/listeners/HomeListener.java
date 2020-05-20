package com.westosia.essentials.bungee.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.bungee.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class HomeListener implements Listener {

    @EventHandler
    public void onReceive(PluginMessageEvent event) {
        // When a player wishes to teleport to a home
        if (event.getTag().equals(Main.getInstance().setHomesChannel)) {
            if (event.getReceiver() instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

                ByteArrayDataInput data = ByteStreams.newDataInput(event.getData());
                UUID uuid = UUID.fromString(data.readUTF());
                String name = data.readUTF();
                String targetServerName = data.readUTF();
                String currentServerName = player.getServer().getInfo().getName();
                // Send player to server with the desired home
                ServerInfo targetServer = ProxyServer.getInstance().getServerInfo(targetServerName);
                if (!targetServerName.equals(currentServerName)) {
                    player.connect(targetServer);
                }
            }
        }
    }
}
