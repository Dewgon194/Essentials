package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

/*
    Author: Jamie Cee
    Description: This command allows players with the permission to heal themselves or a targeted player.
    /heal [player] will heal the targeted player or /heal will heal the user entering the command.

 */


@CommandAlias("heal")
@CommandPermission("essentials.command.heal")
public class HealPlayer extends BaseCommand {

    @Default
    @Description("Heals the player casting the message")
    public void heal(Player player, String[] args) {
        double healthInt = player.getHealth();

        if (args.length == 0) {
            if (healthInt < 20) {
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "You have been healed!");
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You are already at full health");
            }
        } else {
            if (player.hasPermission("essentials.command.heal.players")) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    Player target = Bukkit.getPlayer(args[0]);
                    double healthint = target.getHealth();

                    if (healthint < 20) {
                        target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                        WestosiaAPI.getNotifier().sendChatMessage(target, Notifier.NotifyStatus.SUCCESS, "You have been healed!");
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "You have healed &6" + target.getName());
                    } else {
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Player is already at full health");
                    }
                } else {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Player does not exist");
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Sorry! You do not have permission to perform this command");
            }
        }
    }

}
