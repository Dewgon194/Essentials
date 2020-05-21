package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/*
    Author: Jamie Cee
    Description: This command allows the player with perms to burn other players, and can either set how long for or if they do not enter a time
    in seconds then it defaults to 5 seconds.

 */

@CommandAlias("burn")
@CommandPermission("essentials.command.burn")
public class Burn extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Burns the player that the user enters")
    public void burn(Player player, String[] args) {
        World world = player.getWorld();

        if (args.length == 0) {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Please enter a player to burn");
        } else if (args.length == 2) {
            if (Bukkit.getPlayer(args[0]) != null) {
                if (StringUtils.isNumeric(args[1]) && Integer.parseInt(args[1]) >= 0) {
                    Player target = Bukkit.getPlayer(args[0]);
                    int timeSeconds = Integer.parseInt(args[1]) * 20;
                    target.setFireTicks(timeSeconds);


                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "You have set the following player on fire: &6" + target.getName());
                    WestosiaAPI.getNotifier().sendChatMessage(target, Notifier.NotifyStatus.INFO, "You have been burnt");
                } else {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Please enter a time in seconds");
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Player entered may not be online!");
            }
        } else if (args.length == 1) {
            if (Bukkit.getPlayer(args[0]) != null) {
                Player target = Bukkit.getPlayer(args[0]);
                target.setFireTicks(100);

                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "You have set the following player on fire: &6" + target.getName());
                WestosiaAPI.getNotifier().sendChatMessage(target, Notifier.NotifyStatus.INFO, "You have been burnt");
            }
        }
    }
}