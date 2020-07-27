package com.westosia.essentials.bukkit.virtualappliances;

import com.westosia.essentials.bukkit.Main;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class VirtualAppliance {

    private UUID owner;
    private Container container;

    public VirtualAppliance(UUID owner, Container container) {
        this.owner = owner;
        this.container = container;
    }

    public UUID getOwner() {
        return owner;
    }

    public Container getContainer() {
        return container;
    }

    public void open(Player player) {
        player.openInventory(container.getInventory());
    }

    public void setForceLoadChunk(boolean forceLoad) {
        if (forceLoad) {
            container.getChunk().addPluginChunkTicket(Main.getInstance());
        } else {
            container.getChunk().removePluginChunkTicket(Main.getInstance());
        }
    }
}
