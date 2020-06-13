package com.westosia.essentials.homes;

import com.westosia.essentials.utils.LocationStrings;
import org.bukkit.OfflinePlayer;

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
    }

    public static boolean hasHomesLoaded(UUID uuid) {
        return playerHomes.containsKey(uuid);
    }

    public static Map<String, Home> getHomes(UUID uuid) {
        return playerHomes.get(uuid);
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
    }

    public static void removeHome(Home home) {
        Map<String, Home> homes = getHomes(home.getOwner());
        if (homes != null) {
            homes.remove(home.getName());
        }
    }

    public static void newEntry(UUID uuid) {
        playerHomes.put(uuid, new HashMap<>());
    }

    public static void removePlayer(UUID uuid) {
        playerHomes.remove(uuid);
    }

    public static Home fromString(String string) {
        String locString = string.substring(string.indexOf("("), string.indexOf(")"));
        String[] args = string.replace(locString, "").split("\\|");
        return new Home(UUID.fromString(args[0]), args[1], args[2], LocationStrings.toLoc(locString));
    }
}
