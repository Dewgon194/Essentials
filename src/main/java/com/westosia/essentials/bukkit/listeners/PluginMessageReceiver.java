package com.westosia.essentials.bukkit.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessageReceiver implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!channel.equalsIgnoreCase("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("EssentialsTP")) {
            String source = in.readUTF();
            String target = in.readUTF();
            Bukkit.getPlayer(source).teleport(Bukkit.getPlayer(target)); // Ignore NPEs
        }
    }
}
