package com.westosia.essentials.bukkit.listeners;

import com.westosia.divinityapi.api.DivinityAPI;
import com.westosia.divinityapi.api.events.DivinityChangeEvent;
import com.westosia.westosiaapi.WestosiaAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DivinityChangeListener implements Listener {

    @EventHandler
    public void onDivinityChange(DivinityChangeEvent e) {
        Player player = Bukkit.getPlayer(e.getPlayer());
        if (player != null) {
            player.getInventory().setItem(13, WestosiaAPI.getItemBuilder()
                    .material(Material.CLAY_BALL)
                    .customModelData(6)
                    .name("&3Divinity: &b" + DivinityAPI.getDivinity(player.getUniqueId()))
                    .lore("&7The pure essence of the Gods")
                    .build()
            );
        }
    }

}
