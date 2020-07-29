package com.westosia.essentials.bukkit.listeners;

import com.westosia.westosiaapi.WestosiaAPI;
import net.craftersland.data.bridge.api.events.SyncCompleteEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class AccessoryListener implements Listener {

    private final ItemStack emptyAccessory = WestosiaAPI.getItemBuilder()
            .material(Material.GRAY_STAINED_GLASS_PANE)
            .name("&8Empty Accessory Slot")
            .build();

    @EventHandler
    public void onSync(SyncCompleteEvent e) {
        Inventory inv = e.getPlayer().getInventory();
        // bodged for now
        for (int i = 9; i <= 13; i++) {
            inv.setItem(i, emptyAccessory);
        }

        inv.setItem(13, WestosiaAPI.getItemBuilder()
                .material(Material.CLAY_BALL)
                .customModelData(6)
                .name("&3Divinity: &b100")
                .lore("&7The pure essence of the Gods")
                .build()
        );
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e instanceof PlayerInventory) {
            if (e.getSlot() >= 9 && e.getSlot() <= 13) {
                e.setCancelled(true);
            }
        }
    }

}
