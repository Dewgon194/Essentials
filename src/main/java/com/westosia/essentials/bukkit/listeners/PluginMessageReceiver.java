package com.westosia.essentials.bukkit.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.core.homes.Home;
import com.westosia.essentials.core.homes.HomeManager;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
            // Send a player to a home
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
            // Grab the server name from Bungee; only runs on server enable
            Main.getInstance().serverName = in.readUTF();
        } else if (subChannel.equalsIgnoreCase("EssentialsHomes")) {
            // Loading and saving homes
            String action = in.readUTF();
            if (action.equalsIgnoreCase("load")) {
                // Player has just joined; load from database
                String uuidString = in.readUTF();
                // TODO: database call here to retrieve homes belonging to the uuid
                List<String> homeStrings = Arrays.asList("3ed02f6e-d12c-4040-9eb1-2da94d063cd9|Scone|westosia|(world|-2.53|72.00|168.28|72.20|-20.2)",
                        "3ed02f6e-d12c-4040-9eb1-2da94d063cd9|Brem|westosia|(world|-26.94|87.50|211.45|32.70|0.50)"); // temp homes for testing
                // Tell each server to load this player's homes via Redis
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    homeStrings.forEach(homeString -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().SET_HOME_REDIS_CHANNEL, homeString));
                });
            } else if (action.equalsIgnoreCase("unload")) {
                // Player has left; send Redis message for all servers to unload and save to database
                String uuidString = in.readUTF();
                //TODO: figure out why the map of player homes is null
                // it's probably either to do with OfflinePlayer or that Redis does things async
                if (HomeManager.getHomes(Bukkit.getOfflinePlayer(UUID.fromString(uuidString))) == null) Bukkit.broadcastMessage("player homes is null");
                Collection<Home> homes = HomeManager.getHomes(Bukkit.getOfflinePlayer(UUID.fromString(uuidString))).values();
                Logger.info("unloading " + homes.size() + " homes");
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    // Telling other servers to uncache homes through Redis
                    homes.forEach(home -> {
                        RedisConnector.getInstance().getConnection().publish(Main.getInstance().DEL_HOME_REDIS_CHANNEL, home.toString());
                    });

                    // TODO: Saving homes to database
                });
            }
        }
    }
}
