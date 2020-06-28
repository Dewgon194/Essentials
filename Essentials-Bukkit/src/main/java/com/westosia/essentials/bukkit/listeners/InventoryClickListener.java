package com.westosia.essentials.bukkit.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryClickListener implements Listener {

    public void onClick(InventoryClickEvent e){
        ItemStack is = new ItemStack(Material.PAPER);
        ItemMeta isMeta = is.getItemMeta();
        isMeta.setCustomModelData(1);
        isMeta.setDisplayName(ChatColor.GREEN + "Save Kit");
        is.setItemMeta(isMeta);
        if (e.getCurrentItem() != null && e.getCurrentItem().equals(is)){
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();

        }
    }
}
