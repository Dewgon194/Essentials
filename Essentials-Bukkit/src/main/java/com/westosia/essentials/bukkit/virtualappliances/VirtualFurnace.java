package com.westosia.essentials.bukkit.virtualappliances;

import com.westosia.essentials.bukkit.Main;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class VirtualFurnace {

    private UUID owner;
    private Furnace furnace;

    public VirtualFurnace(UUID owner, Furnace furnace) {
        this.owner = owner;
        this.furnace = furnace;
    }

    public UUID getOwner() {
        return owner;
    }

    public Furnace getFurnace() {
        return furnace;
    }

    public void open(Player player) {
        player.openInventory(getFurnace().getInventory());
    }

    public void setForceLoadChunk(boolean forceLoad) {
        if (forceLoad) {
            furnace.getChunk().addPluginChunkTicket(Main.getInstance());
        } else {
            furnace.getChunk().removePluginChunkTicket(Main.getInstance());
        }
    }

    public static VirtualFurnace getFurnace(Furnace furnace) {
        Collection<PlayerAppliances> appliances = PlayerAppliances.getAllAppliances();
        for (PlayerAppliances applianceSet : appliances) {
            VirtualFurnace vurnace = applianceSet.getFurnace();
            if (vurnace != null && furnace.equals(vurnace.getFurnace())) {
                return vurnace;
            }
        }
        return null;
    }
}
