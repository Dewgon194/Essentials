package com.westosia.essentials.bukkit.virtualappliances;

import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;

import java.util.Collection;
import java.util.UUID;

public class VirtualBrewingStand extends VirtualAppliance {
//TODO: make this into a generic (?)
    public VirtualBrewingStand(UUID owner, BrewingStand container) {
        super(owner, container);
    }

    public BrewingStand getBrewingStand() {
        return (BrewingStand) getContainer();
    }

    public static VirtualBrewingStand getBrewingStand(BrewingStand brewingStand) {
        Collection<ApplianceManager> appliances = ApplianceManager.getAllAppliances();
        for (ApplianceManager applianceSet : appliances) {
            VirtualBrewingStand virtualBrewingStand = applianceSet.getBrewingStand();
            if (brewingStand != null && brewingStand.equals(virtualBrewingStand.getBrewingStand())) {
                return virtualBrewingStand;
            }
        }
        return null;
    }
}
