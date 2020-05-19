package com.westosia.essentials.homes;

import com.westosia.essentials.utils.LocationStrings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Home {
    private UUID owner;
    private String name, server;
    private Location location;

    public Home(Player player, String name) {
        owner = player.getUniqueId();
        this.name = name;
        server = player.getServer().getName();
        location = player.getLocation();
    }

    Home(UUID uuid, String name, String server, Location location) {
        owner = uuid;
        this.name = name;
        this.server = server;
        this.location = location;
    }

    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(owner);
    }

    public String getName() {
        return name;
    }

    public String getServerName() {
        return server;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return owner.toString() + "|" +
                name + "|" +
                server + "|" +
                LocationStrings.toString(getLocation());
    }
}
