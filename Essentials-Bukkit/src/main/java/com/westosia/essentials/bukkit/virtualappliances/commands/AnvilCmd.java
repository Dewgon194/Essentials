package com.westosia.essentials.bukkit.virtualappliances.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

@CommandAlias("anvil")
@CommandPermission("essentials.command.anvil")
public class AnvilCmd extends BaseCommand {

    @Default
    @Description("Opens an anvil for the player")
    public void anvil(Player player) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        // Create the anvil
        ContainerAnvil container = new ContainerAnvil(0, nmsPlayer.inventory, ContainerAccess.at(nmsPlayer.world, new BlockPosition(0, 0, 0)));
        container.checkReachable = false;
        // Make sure to set a title, otherwise some plugins complain
        container.setTitle(new ChatMessage("Repairing"));
        // Something NMS needs to keep track of invs
        int id = nmsPlayer.nextContainerCounter();
        // Create and send anvil packet
        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(id, Containers.ANVIL, new ChatMessage("Repairing"));
        nmsPlayer.playerConnection.sendPacket(packet);
        nmsPlayer.activeContainer = container;
    }
}
