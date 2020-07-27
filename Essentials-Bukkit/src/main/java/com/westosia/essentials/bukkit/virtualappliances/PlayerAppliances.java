package com.westosia.essentials.bukkit.virtualappliances;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerAppliances {

    private UUID uuid;
    private VirtualFurnace furnace;

    private static Map<UUID, PlayerAppliances> appliances;

    public PlayerAppliances(UUID uuid) {
        this.uuid = uuid;
        appliances.put(uuid, this);
    }

    public VirtualFurnace getFurnace() {
        return furnace;
    }

    public void setFurnace(VirtualFurnace furnace) {
        this.furnace = furnace;
    }

    public static void load() {
        appliances = new HashMap<>();
    }

    public static PlayerAppliances getAppliances(UUID uuid) {
        PlayerAppliances pa = appliances.get(uuid);
        if (pa == null) {
            pa = new PlayerAppliances(uuid);
        }
        return pa;
    }

    public static Collection<PlayerAppliances> getAllAppliances() {
        return appliances.values();
    }
}
