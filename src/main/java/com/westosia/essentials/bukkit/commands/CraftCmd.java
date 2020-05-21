package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/*
    Author: Jamie Cee
    Description: This command allows players to open a virtual crafting table and use it fully.

 */

@CommandAlias("craft|crafting|workbench|wb")
@CommandPermission("essentials.command.craft")
public class CraftCmd extends BaseCommand {

    @Default
    @Description("Opens a crafting table for the player")
    public void craft(Player player) {

        player.openWorkbench(player.getLocation(), true);

    }
}
