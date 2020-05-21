package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


/*
    Author: Jamie Cee
    Description: This command allows players with the permission to smite a targetted block or a targetted player.
    Not entering a parameter for a player e.g. just doing /smite will send a lightning strike to where the player is aiming.
    Entering an existing player that is currently on the server e.g. /smite [player] will smite that player.

 */

@CommandAlias("smite|smitten")
@CommandPermission("essentials.command.smite")
public class SmiteCmd extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Smite target block or target player")
    public void smite(Player player, String[] args) {
        World world = player.getWorld();

        if (args.length == 0) {
            Block targetblock = player.getTargetBlock(null, 50);
            Location location = targetblock.getLocation();
            world.strikeLightning(location);
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "May the lord smite thee down!");
        } else if (args.length == 1) {
            if (Bukkit.getPlayer(args[0]) != null) {
                Player target = Bukkit.getPlayer(args[0]);
                Location loc = target.getLocation();

                world.strikeLightning(loc);
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Thou has casted the almighty smite to &6" + target.getName());
                WestosiaAPI.getNotifier().sendChatMessage(target, Notifier.NotifyStatus.INFO, "Thou hath been smitten!");

            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Player entered may not be online!");
            }
        } else if (args.length > 1) {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You have entered too many arguments!");
        }
    }
}
