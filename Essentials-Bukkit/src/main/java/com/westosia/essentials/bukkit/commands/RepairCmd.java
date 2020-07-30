package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.cooldownapi.storage.Cooldown;
import com.westosia.cooldownapi.storage.CooldownDB;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;


@CommandAlias("repair")
@CommandPermission("essentials.command.repair")
public class RepairCmd extends BaseCommand {

    @Default
    @Description("Repairs the item in the players hand or entire inventory if argument 'all' is added")
    public void repair(Player player, String[] args) {
        Cooldown cooldown = Cooldown.getCooldown(player, "repair");
        if (cooldown == null || cooldown.isExpired()) {
            ItemMeta mainHMeta = player.getInventory().getItemInMainHand().getItemMeta();
            if (args.length == 0 && player.getInventory().getItemInMainHand().getType() != null && mainHMeta instanceof Damageable) {
                Short maxDurability = player.getInventory().getItemInMainHand().getType().getMaxDurability();
                if (0 == ((Damageable) player.getInventory().getItemInMainHand().getItemMeta()).getDamage() && maxDurability > 1) {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The item is already in good condition!");
                } else {
                    if (maxDurability > 0) {
                        ((Damageable) mainHMeta).setDamage(0);
                        player.getInventory().getItemInMainHand().setItemMeta(mainHMeta);
                        long duration = Cooldown.getDurationFromPermission(player, "essentials.command.repair.");
                        Cooldown newCooldown = new Cooldown(player, "repair", (int) duration);
                        newCooldown.save();
                        CooldownDB.getInstance().saveCooldown(newCooldown);
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Your item has been repaired!");
                    } else {
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You can only repair an item that has durability!");
                    }

                }
            } else if (args.length == 1 && args[0].equals("all")) {
                Inventory allInv = player.getInventory();
                int i;
                for (i = 0; i < allInv.getSize(); i++) {
                    if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).hasItemMeta() && player.getInventory().getItem(i).getItemMeta() instanceof Damageable) {
                        ItemMeta tempMeta = player.getInventory().getItem(i).getItemMeta();
                        ((Damageable) tempMeta).setDamage(0);
                        player.getInventory().getItem(i).setItemMeta(tempMeta);
                    }
                }
                long duration = Cooldown.getDurationFromPermission(player, "essentials.command.repair.");
                Cooldown newCooldown = new Cooldown(player, "repair", (int) duration);
                newCooldown.save();
                CooldownDB.getInstance().saveCooldown(newCooldown);
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "All of your items have been repaired!");
            } else if (args.length > 1) {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You have entered too many arguments!");
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You can only repair an item that has durability!");
            }
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Command on cooldown! Remaining time: &f" + cooldown.getFormattedTimeLeft());
        }
    }
}
