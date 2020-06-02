package com.westosia.essentials.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class PostLoginListener implements Listener {

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), () -> sendLoadRequest(event.getPlayer()), 1, TimeUnit.SECONDS);
    }

    private void sendLoadRequest(ProxiedPlayer player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("EssentialsHomes");
        out.writeUTF("load");
        out.writeUTF(player.getUniqueId().toString());

        if (player.getServer() != null && player.getServer().getInfo() != null) {
            player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
        }
    }
}
