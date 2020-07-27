package com.westosia.essentials.bukkit.virtualappliances;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ApplianceManager {

    private UUID uuid;
    private VirtualFurnace furnace;
    private VirtualBrewingStand brewingStand;

    private static Map<UUID, ApplianceManager> appliances;

    public ApplianceManager(UUID uuid) {
        this.uuid = uuid;
        appliances.put(uuid, this);
    }

    public VirtualFurnace getFurnace() {
        return furnace;
    }

    public VirtualBrewingStand getBrewingStand() {
        return brewingStand;
    }

    public void setFurnace(VirtualFurnace furnace) {
        this.furnace = furnace;
    }

    public void setBrewingStand(VirtualBrewingStand brewingStand) {
        this.brewingStand = brewingStand;
    }

    public static void load() {
        appliances = new HashMap<>();
    }

    public static ApplianceManager getAppliances(UUID uuid) {
        ApplianceManager pa = appliances.get(uuid);
        if (pa == null) {
            pa = new ApplianceManager(uuid);
        }
        return pa;
    }

    public static Collection<ApplianceManager> getAllAppliances() {
        return appliances.values();
    }
}
