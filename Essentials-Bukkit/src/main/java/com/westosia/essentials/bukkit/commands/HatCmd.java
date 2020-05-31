package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/*
    Author: Jamie Cee
    Description: This command allows the user to place an item in their hand onto their head.
 */

@CommandAlias("hat")
@CommandPermission("essentials.command.hat")
public class HatCmd extends BaseCommand {

    @Default
    @Description("Sets item in players hand as a hat")
    public void hat(Player player) {
        final ItemStack hand = player.getInventory().getItemInMainHand();
        final ItemStack head = player.getInventory().getHelmet();

        if (hand == null || hand.getType() == Material.AIR) {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You must have an item in your hand to quip it as a hat");
        }

        player.getInventory().setHelmet(hand);
        player.getInventory().remove(hand);

        if (head != null && head.getType() != Material.AIR) {
            player.getInventory().addItem(head);
        }
    }
}
