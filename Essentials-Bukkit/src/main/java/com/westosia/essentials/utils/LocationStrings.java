package com.westosia.essentials.utils;

import com.westosia.essentials.bukkit.Main;
import com.westosia.westosiaapi.utils.Text;
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

    public static String friendlyLoc(Location location, boolean includeServer) {
        StringBuilder stringBuilder = new StringBuilder();
        if (includeServer) {
            stringBuilder.append(Text.colour("&6" + Main.getInstance().SERVER_NAME + " &a@ "));
        }
        stringBuilder.append(Text.colour("&f" + location.getWorld().getName() + "&a, "));
        stringBuilder.append(Text.colour("&f" + location.getBlockX() + "&a, "));
        stringBuilder.append(Text.colour("&f" + location.getBlockY() + "&a, "));
        stringBuilder.append(Text.colour("&f" + location.getBlockZ()));
        return stringBuilder.toString();
    }
    private static double round(double number) {
        return new BigDecimal(number).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
