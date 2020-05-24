package com.westosia.essentials.bungee.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        sendUnloadRequest(event.getPlayer());
    }

    private void sendUnloadRequest(ProxiedPlayer player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("EssentialsHomes");
        out.writeUTF("unload");
        out.writeUTF(player.getUniqueId().toString());

        player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
    }
}
