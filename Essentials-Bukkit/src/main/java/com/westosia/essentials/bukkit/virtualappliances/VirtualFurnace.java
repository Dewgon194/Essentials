package com.westosia.essentials.bukkit.virtualappliances;

import org.bukkit.block.Furnace;

import java.util.Collection;
import java.util.UUID;

public class VirtualFurnace extends VirtualAppliance {

    public VirtualFurnace(UUID owner, Furnace furnace) {
        super(owner, furnace);
    }

    public Furnace getFurnace() {
        return (Furnace) getContainer();
    }

    public static VirtualFurnace getFurnace(Furnace furnace) {
        Collection<ApplianceManager> appliances = ApplianceManager.getAllAppliances();
        for (ApplianceManager applianceSet : appliances) {
            VirtualFurnace vurnace = applianceSet.getFurnace();
            if (vurnace != null && furnace.equals(vurnace.getFurnace())) {
                return vurnace;
            }
        }
        return null;
    }
}
