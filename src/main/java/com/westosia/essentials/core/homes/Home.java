package com.westosia.essentials.core.homes;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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
        //TODO: make this get actual server from Bungee
        server = player.getServer().getName();
        location = player.getLocation();
    }

    Home(UUID uuid, String name, String server, Location location) {
        owner = uuid;
        this.name = name;
        this.server = server;
        //TODO: round locations to two decimal places, we don't need massive .763892384843989
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

    public byte[] toByteArray() {
        ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
        bytes.writeUTF(owner.toString());
        bytes.writeUTF(getName());
        bytes.writeUTF(getServerName());
        bytes.writeUTF(LocationStrings.toString(getLocation()));
        return bytes.toByteArray();
    }

    public void use() {
        Player player = getOwner().getPlayer();
        if (player != null) {
            player.teleport(getLocation());
        }
    }
}
