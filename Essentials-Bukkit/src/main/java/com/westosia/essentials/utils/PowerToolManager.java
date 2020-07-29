package com.westosia.essentials.utils;

import com.westosia.essentials.bukkit.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PowerToolManager {
    private static Map<UUID, Map<Material, String>> powertools = new HashMap<>();

    private static final String METADATA = "powertools";

    public static boolean hasPowerToolsEnabled(Player player) {
        return player.hasMetadata(METADATA);
    }

    public static void setHasPowerToolsEnabled(Player player, boolean enabled) {
        if (enabled) {
            if (!hasPowerToolsEnabled(player)) {
                player.setMetadata(METADATA, new FixedMetadataValue(Main.getInstance(), true));
            }
        } else {
            if (hasPowerToolsEnabled(player)) {
                player.removeMetadata(METADATA, Main.getInstance());
            }
        }
    }

    public static void cachePowerTool(UUID uuid, Material material, String cmd) {
        Map<Material, String> tools = powertools.get(uuid);
        if (tools == null) {
            tools = new HashMap<>();
        }
        tools.put(material, cmd);
        powertools.put(uuid, tools);
    }

    public static void cacheAll(UUID uuid, Map<Material, String> powertools) {
        PowerToolManager.powertools.put(uuid, powertools);
    }

    public static void uncachePowerTool(UUID uuid, Material material) {
        Map<Material, String> tools = powertools.get(uuid);
        if (tools != null) {
            tools.remove(material);
        }
        powertools.put(uuid, tools);
    }

    public static void uncacheAll(UUID uuid) {
        powertools.remove(uuid);
    }

    public static Map<Material, String> getPowerTools(Player player) {
        return powertools.getOrDefault(player.getUniqueId(), new HashMap<>());
    }

    public static boolean isPowerTool(Player player, Material material) {
        return getPowerTools(player).containsKey(material);
    }
}
