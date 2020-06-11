package com.westosia.essentials.bukkit.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

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
            // Send a player to a home
            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);
            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            try {
                String homeString = msgin.readUTF();
                Home home = HomeManager.fromString(homeString);
                player.teleport(home.getLocation());
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Teleported to home &f" + home.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (subChannel.equalsIgnoreCase("GetServer")) {
            // Grab the server name from Bungee; only runs on server enable
            Main.getInstance().serverName = in.readUTF();
        } else if (subChannel.equalsIgnoreCase("EssentialsHomes")) {
            // Loading homes
            String action = in.readUTF();
            if (action.equalsIgnoreCase("load")) {
                // Player has just joined; load from database
                Logger.info("loading homes");
                String uuidString = in.readUTF();
                // Only load if they aren't already loaded
                if (HomeManager.getHomes(UUID.fromString(uuidString)) == null) {
                    Logger.info(uuidString  + " needs homes loaded");
                    // Tell each server to load this player's homes via Redis
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                        // Get homes from database
                        Collection<Home> dbHomes = DatabaseEditor.getHomesInDB(UUID.fromString(uuidString)).values();
                        if (!dbHomes.isEmpty()) {
                            Logger.info(uuidString  + " loading homes from db");
                            dbHomes.forEach(home -> RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_HOME, home.toString()));
                        } else {
                            Logger.info(uuidString  + " no homes found, creating empty entry");
                            RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_HOME, uuidString);
                        }
                        //dbHomes.forEach(home -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().SET_HOME_REDIS_CHANNEL, home.toString()));
                    });
                }
            }
        }
    }
}
