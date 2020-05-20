package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

/*
    Author: Jamie Cee
    Description: This command allows players with the permission to feed themselves or a targeted player.
    /feed [player] will feed the targeted player or /feed will feed the user entering the command.

 */

@CommandAlias("feed")
@CommandPermission("essentials.command.feed")
public class FeedPlayer extends BaseCommand {

    @Default
    @Description("Feeds the player casting the message")
    public void feed(Player player, String[] args) {
        double feedInt = player.getFoodLevel();

        if (args.length == 0) {
            if (feedInt < 20) {
                player.setFoodLevel(20);
                if (player.hasPermission("essentials.command.feed")) {
                    player.setSaturation(20);
                }
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "You have been fed!");
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You are already at full food");
            }
        } else {
            if (player.hasPermission("essentials.command.feed.players")) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    Player target = Bukkit.getPlayer(args[0]);
                    double foodint = target.getFoodLevel();

                    if (foodint < 20) {
                        target.setFoodLevel(20);
                        if (player.hasPermission("essentials.command.feed.saturation")) {
                            target.setSaturation(20);
                        }
                        WestosiaAPI.getNotifier().sendChatMessage(target, Notifier.NotifyStatus.SUCCESS, "You have been fed!");
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "You have fed &6" + target.getName());
                    } else {
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Player is already at full food level");
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