package com.westosia.essentials.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationStrings {

    public static Location toLoc(String string) {
        String[] split = string.substring(1, string.length() - 1).split("\\|");
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]),
                Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }

    public static String toString(Location location) {
        return "(" +
                location.getWorld().getName() + "|" +
                location.getX() + "|" +
                location.getY() + "|" +
                location.getZ() + "|" +
                location.getYaw() + "|" +
                location.getPitch() +
                ")";
    }
}
