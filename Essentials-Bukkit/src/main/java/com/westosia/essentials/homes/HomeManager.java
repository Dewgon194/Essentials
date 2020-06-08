package com.westosia.essentials.homes;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.utils.LocationStrings;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HomeManager {
    // Maps a player ID to all their homes, which are each mapped by their name
    private static Map<UUID, Map<String, Home>> playerHomes = new HashMap<>();

    public static Set<UUID> getCachedHomeOwners() {
        return playerHomes.keySet();
    }

    public static Map<String, Home> getHomes(OfflinePlayer player) {
        return playerHomes.get(player.getUniqueId());
        //return playerHomes.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
    }

    public static boolean hasHomesLoaded(UUID uuid) {
        return playerHomes.containsKey(uuid);
    }

    public static Map<String, Home> getHomes(UUID uuid) {
        return playerHomes.get(uuid);
        //return playerHomes.computeIfAbsent(uuid, k -> new HashMap<>());
    }

    public static Home getHome(OfflinePlayer player, String home) {
        Map<String, Home> homes = getHomes(player);
        if (homes != null) {
            return homes.get(home);
        }
        return null;
    }

    public static void cacheHome(Home home) {
        Map<String, Home> homes = getHomes(home.getOwner());
        if (homes == null) {
            homes = new HashMap<>();
        }
        homes.put(home.getName(), home);
        playerHomes.put(home.getOwner().getUniqueId(), homes);
        //playerHomes.replace(home.getOwner().getUniqueId(), homes);
    }

    public static void removeHome(Home home) {
        Map<String, Home> homes = getHomes(home.getOwner());
        if (homes != null) {
            homes.remove(home.getName());
        }
    }

    public static void removePlayer(UUID uuid) {
        playerHomes.remove(uuid);
    }

    public static Home fromString(String string) {
        String locString = string.substring(string.indexOf("("), string.indexOf(")"));
        String[] args = string.replace(locString, "").split("\\|");
        return new Home(UUID.fromString(args[0]), args[1], args[2], LocationStrings.toLoc(locString));
    }

    public static void sendHomeData(Home home, Player playerUsing) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ForwardToPlayer");
        out.writeUTF(playerUsing.getName());
        out.writeUTF("EssentialsSendToHome");

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(home.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        playerUsing.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }
}
