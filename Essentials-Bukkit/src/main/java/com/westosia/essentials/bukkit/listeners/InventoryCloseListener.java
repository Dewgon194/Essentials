package com.westosia.essentials.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {
    @EventHandler
    public void onInvClose(InventoryCloseEvent e){
        if (e.getView().getTitle().contains("JoinKit")){

        }
    }
}
