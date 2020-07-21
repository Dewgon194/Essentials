package com.westosia.essentials.bukkit.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.westosia.databaseapi.database.DatabaseConnector;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.LocationStrings;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import com.westosia.westosiaapi.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.time.Instant;
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
                    long timeSince = Instant.now().getEpochSecond() - DatabaseEditor.getLastSeen(targetUUID);
                    String location = DatabaseEditor.getLastLocation(targetUUID);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> WestosiaAPI.getNotifier().sendChatMessage(Bukkit.getPlayer(senderName), Notifier.NotifyStatus.SUCCESS, "Last seen &e" + getFriendlyDuration(timeSince) + "&a ago at " + location));
                });
            } else {
                String location = LocationStrings.friendlyLoc(Bukkit.getPlayer(targetUUID).getLocation(), true);
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    long timeSince = Instant.now().getEpochSecond() - DatabaseEditor.getLastSeen(targetUUID);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                        if (Bukkit.getPlayer(senderName) != null) {
                            // Is on the same server, send normal message
                            WestosiaAPI.getNotifier().sendChatMessage(Bukkit.getPlayer(senderName), Notifier.NotifyStatus.SUCCESS, "Online for &e" + getFriendlyDuration(timeSince) + "&a, at " + location);
                        } else {
                            ByteArrayDataOutput output = ByteStreams.newDataOutput();
                            output.writeUTF("Message");
                            output.writeUTF(senderName);
                            output.writeUTF(Text.colour(Notifier.NotifyStatus.SUCCESS.getPrefix() + "Online for &e" + getFriendlyDuration(timeSince) + "&a, at " + location));
                            Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", output.toByteArray());
                        }
                    });
                    //Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> WestosiaAPI.getNotifier().sendChatMessage(Bukkit.getPlayer(senderName), Notifier.NotifyStatus.SUCCESS, "Online for &e" + getFriendlyDuration(timeSince) + "&a, at " + location));
                });
            }
        }
    }

    private String getFriendlyDuration(long time) {
        int hours = (int) time / 3600;
        int minutes = (int) (time % 3600) / 60;
        int seconds = (int) time % 60;

        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }
}
