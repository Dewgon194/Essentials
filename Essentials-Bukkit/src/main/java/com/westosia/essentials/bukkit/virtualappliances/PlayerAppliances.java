package com.westosia.essentials.bukkit.virtualappliances;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

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

    public void openFurnace() {
        EntityPlayer nmsPlayer = ((CraftPlayer) (Bukkit.getPlayer(uuid))).getHandle();
        if (furnace == null) {
            furnace = new VirtualFurnace(nmsPlayer);
        }
        nmsPlayer.openContainer(furnace);
    }

    public static void load() {
        appliances = new HashMap<>();
    }

    public static PlayerAppliances getAppliances(UUID uuid) {
        return appliances.getOrDefault(uuid, new PlayerAppliances(uuid));
    }

    public static Collection<PlayerAppliances> getAllAppliances() {
        return appliances.values();
    }
}
