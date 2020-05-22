package com.westosia.essentials.bukkit.commands.gamemodes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/*
    Author: Jamie Cee

    Description: This command allows the player to change gamemode to Adventure and players with admin perms
    to change other players game modes.
 */

@CommandAlias("gma")
@CommandPermission("essentials.command.gamemode")
public class GamemodeAdventureCmd extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Change player gamemode to adventure")
    public void gamemodeAdventure(Player player, String[] args) {

        if (args.length == 0) {
            if (player.hasPermission("essentials.command.gamemode")) {
                if (player.getGameMode().equals(GameMode.ADVENTURE)) {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You are already in adventure mode");
                } else {
                    player.setGameMode(GameMode.ADVENTURE);
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "GameMode changed to adventure");
                }
            }
        } else if (player.hasPermission("essentials.command.gamemode.admin")) {
            if (Bukkit.getPlayer(args[0]) != null) {
                Player target = Bukkit.getPlayer(args[0]);
                if (!(target.getGameMode().equals(GameMode.ADVENTURE))) {
                    target.setGameMode(GameMode.ADVENTURE);
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, target.getName() + " is now in adventure");
                } else {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, target.getName() + " is already in adventure");
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Player does not exist");
            }
        }
    }
}
