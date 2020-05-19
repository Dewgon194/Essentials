package com.westosia.essentials.homes;

import com.westosia.essentials.homes.Home;
import com.westosia.essentials.utils.LocationStrings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeManager {
    // Maps a player ID to all their homes, which are each mapped by their name
    private static Map<UUID, Map<String, Home>> playerHomes = new HashMap<>();

    public static Map<String, Home> getHomes(OfflinePlayer player) {
        return playerHomes.get(player.getUniqueId());
    }

    public static Home getHome(OfflinePlayer player, String home) {
        Map<String, Home> homes = playerHomes.get(player.getUniqueId());
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
    }

    public static void removeHome(Player player, String home) {
        getHomes(player).remove(home);
    }

    public static Home fromString(String string) {
        String locString = string.substring(string.indexOf("("), string.indexOf(")"));
        String[] args = string.replace(locString, "").split("\\|");
        return new Home(UUID.fromString(args[0]), args[1], args[2], LocationStrings.toLoc(locString));
    }
}
