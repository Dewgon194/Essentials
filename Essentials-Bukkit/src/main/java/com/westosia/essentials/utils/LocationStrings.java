package com.westosia.essentials.utils;

import com.westosia.essentials.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public static Location shrinkLoc(Location location) {
        location = location.set(round(location.getX()), round(location.getY()), round(location.getZ()));
        location.setYaw((float) round(location.getYaw()));
        location.setPitch((float) round(location.getPitch()));
        return location;
    }

    public static Location fromConfig(String directory) {
        String locString = Main.getInstance().getConfig().getString(directory);
        if (locString == null) {
            return new Location(Bukkit.getWorlds().get(0), 0, 64, 0);
        } else {
            return toLoc(locString);
        }
    }

    private static double round(double number) {
        return new BigDecimal(number).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
