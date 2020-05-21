package com.westosia.essentials.bukkit.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.core.homes.Home;
import com.westosia.essentials.core.homes.HomeManager;
import com.westosia.essentials.utils.LocationStrings;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

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
        } else if (subChannel.equalsIgnoreCase("EssentialsSendToHome")) {
            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);
            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            try {
                String homeString = msgin.readUTF();
                Home home = HomeManager.fromString(homeString);
                home.use();
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Teleported to home &f" + home.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (subChannel.equalsIgnoreCase("GetServer")) {
            Main.getInstance().serverName = in.readUTF();
        }
    }
}
