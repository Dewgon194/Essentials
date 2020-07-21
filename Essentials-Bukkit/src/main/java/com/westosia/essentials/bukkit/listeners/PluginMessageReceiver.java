package com.westosia.essentials.bukkit.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.LocationStrings;
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
            Player playerSource = Bukkit.getPlayer(source);
            if (playerSource != null) {
                // target is a location
                if (target.contains("|")) {
                    // add pitch, yaw
                    target = "(" + target + "|" + playerSource.getLocation().getYaw() + "|" + playerSource.getLocation().getPitch() + ")";
                    // replacing the first word (the server name) with the world the player is on
                    String bukkitLoc = target.replace(target.substring(1, target.indexOf("|")), playerSource.getWorld().getName());
                    Location location = LocationStrings.toLoc(bukkitLoc);
                    player.teleport(location);
                } else {
                    // target is a player
                    if (Bukkit.getPlayer(target) != null) {
                        playerSource.teleport(Bukkit.getPlayer(target));
                    }
                }
            }
        } else if (subChannel.equalsIgnoreCase("GetServer")) {
            // Grab the server name from Bungee; only runs on server enable
            Main.getInstance().SERVER_NAME = in.readUTF();
        } else if (subChannel.equalsIgnoreCase("querySeen")) {
            String mode = in.readUTF();
            String senderName = in.readUTF();
            String targetName = in.readUTF();
            UUID targetUUID = Bukkit.getPlayerUniqueId(targetName);
            if (mode.equalsIgnoreCase("offline")) {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    long seen = DatabaseEditor.getLastSeen(targetUUID);
                    //TODO: database location
                    String location = "a valid location";
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> WestosiaAPI.getNotifier().sendChatMessage(Bukkit.getPlayer(senderName), Notifier.NotifyStatus.SUCCESS, "Last seen " + seen + " at " + location));
                });
            } else {
                String location = Bukkit.getPlayer(targetUUID).getLocation().toString();
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    long seen = DatabaseEditor.getLastSeen(targetUUID);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> WestosiaAPI.getNotifier().sendChatMessage(Bukkit.getPlayer(senderName), Notifier.NotifyStatus.SUCCESS, "Online for " + seen + " at " + location));
                });
            }
        }
    }
}
