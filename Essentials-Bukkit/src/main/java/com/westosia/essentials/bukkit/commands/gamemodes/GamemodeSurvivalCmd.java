package com.westosia.essentials.bukkit.commands.gamemodes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/*
    Author: Jamie Cee

    Description: This command allows the player to change gamemode to Survival and players with admin perms
    to change other players gamemodes.
 */

@CommandAlias("gms")
@CommandPermission("essentials.command.gamemode")
public class GamemodeSurvivalCmd extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Change player gamemode to survival")
    public void gamemodeSurvival(Player player, String[] args) {

        if (args.length == 0) {
            if (player.hasPermission("essentials.command.gamemode")) {
                if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You are already in survival mode");
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Gamemode changed to survival");
                }
            }
        } else if (player.hasPermission("essentials.command.gamemode.admin")) {
            if (Bukkit.getPlayer(args[0]) != null) {
                Player target = Bukkit.getPlayer(args[0]);
                if (!(target.getGameMode().equals(GameMode.SURVIVAL))) {
                    target.setGameMode(GameMode.SURVIVAL);
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, target.getName() + " is now in survival");
                } else {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, target.getName() + " is already in survival");
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Player does not exist");
            }
        }
    }
}
