package com.westosia.essentials.bukkit.listeners;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.bukkit.virtualappliances.VirtualFurnace;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;

public class FurnaceSmeltListener implements Listener {
    //TODO: make furnaces unload chunks whenever they finish smelting
/*
    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        Furnace furnace = (Furnace) event.getBlock().getState();
        FurnaceInventory inv = furnace.getInventory();
        if (inv.getSmelting() == null || inv.getSmelting().getType() == Material.AIR) {
            VirtualFurnace vurnace = VirtualFurnace.getFurnace(furnace);
            // Nothing is smelting and this is a virtual furnace, unload chunk
            if (vurnace != null) {
                vurnace.setForceLoadChunk(false);
            }
        }
    }*/
/*
    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {
        Furnace furnace = (Furnace) event.getBlock();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            if (furnace.i)
        }, event.getBurnTime());
        Logger.broadcast("burn fire");
    }*/
}
