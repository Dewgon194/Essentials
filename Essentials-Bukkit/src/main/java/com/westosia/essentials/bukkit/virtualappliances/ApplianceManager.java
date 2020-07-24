package com.westosia.essentials.bukkit.virtualappliances;

import net.minecraft.server.v1_15_R1.TileEntity;
import net.minecraft.server.v1_15_R1.TileEntityContainer;

import java.util.*;

public class ApplianceManager {
    private static Map<UUID, Map<ApplianceType, ? extends TileEntity>> appliances;

    public static VirtualFurnace getFurnace(UUID uuid) {
        return (VirtualFurnace) appliances.get(uuid).get(ApplianceType.FURNACE);
    }

    public static Collection<? extends TileEntity> getAppliances(UUID uuid) {
        return appliances.get(uuid).values();
    }

    public static void load() {
        appliances = new HashMap<>();
    }

    public enum ApplianceType {
        FURNACE(0), BREWING_STAND(1);
        private int type;
        ApplianceType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}
