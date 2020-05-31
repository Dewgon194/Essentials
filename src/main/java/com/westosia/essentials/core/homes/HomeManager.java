package com.westosia.essentials.core.homes;

import com.westosia.essentials.utils.LocationStrings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
        return playerHomes.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
    }

    public static Map<String, Home> getHomes(UUID uuid) {
        return playerHomes.computeIfAbsent(uuid, k -> new HashMap<>());
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
        homes.put(home.getName(), home);
        playerHomes.replace(home.getOwner().getUniqueId(), homes);
        //home.getOwner().getPlayer().sendMessage(getHomes(home.getOwner()).size() + " homes now");
    }

    public static void removeHome(Home home) {
        getHomes(home.getOwner()).remove(home.getName());
    }

    public static Home fromString(String string) {
        String locString = string.substring(string.indexOf("("), string.indexOf(")"));
        String[] args = string.replace(locString, "").split("\\|");
        return new Home(UUID.fromString(args[0]), args[1], args[2], LocationStrings.toLoc(locString));
    }
}
