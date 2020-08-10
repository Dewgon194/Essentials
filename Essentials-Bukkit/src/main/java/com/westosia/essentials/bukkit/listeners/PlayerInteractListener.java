package com.westosia.essentials.bukkit.listeners;

import com.westosia.essentials.utils.PowerToolManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material material = event.getMaterial();
        if (event.hasItem() && PowerToolManager.isPowerTool(player, material) && PowerToolManager.hasPowerToolsEnabled(player)) {
            String cmd = PowerToolManager.getPowerTools(player).get(material);
            player.performCommand(cmd);
            event.setCancelled(true);
        }
    }
}
