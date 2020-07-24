package com.westosia.essentials.bukkit.virtualappliances.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import net.minecraft.server.v1_15_R1.*;

import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventoryBrewer;
import org.bukkit.entity.Player;

@CommandAlias("brew|brewingstand|bs|brewing")
@CommandPermission("essentials.command.anvil")
public class BrewCmd extends BaseCommand {

    @Default
    @Description("Opens a brewing stand for the player")
    public void brew(Player player) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        // Create the brewing stand
        TileEntityBrewingStand te = new TileEntityBrewingStand();
        ContainerBrewingStand container = new ContainerBrewingStand(0, nmsPlayer.inventory, te, new ContainerProperties(5));
        container.checkReachable = false;
        // Make sure to set a title, otherwise some plugins complain
        container.setTitle(new ChatMessage("Brewing"));
        // Something NMS needs to keep track of invs
        int id = nmsPlayer.nextContainerCounter();
        // Create and send anvil packet
        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(id, Containers.BREWING_STAND, new ChatMessage("Brewing"));
        nmsPlayer.playerConnection.sendPacket(packet);
        nmsPlayer.activeContainer = container;
    }
}
