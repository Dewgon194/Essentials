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

    Description: This command allows the player to change gamemode to Creative and players with admin perms
    to change other players gamemodes.
 */

@CommandAlias("gmc")
@CommandPermission("essentials.command.gamemode")
public class GamemodeCreativeCmd extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Change player gamemode to creative")
    public void gamemodeCreative(Player player, String[] args) {

        if (args.length == 0) {
            if (player.hasPermission("essentials.command.gamemode")) {
                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You are already in creative mode");
                } else {
                    player.setGameMode(GameMode.CREATIVE);
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Gamemode changed to creative");
                }
            }
        } else if (player.hasPermission("essentials.command.gamemode.admin")) {
            if (Bukkit.getPlayer(args[0]) != null) {
                Player target = Bukkit.getPlayer(args[0]);
                if (!(target.getGameMode().equals(GameMode.CREATIVE))) {
                    target.setGameMode(GameMode.CREATIVE);
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, target.getName() + " is now in creative");
                } else {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, target.getName() + " is already in creative");
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Player does not exist");
            }
        }
    }
}
