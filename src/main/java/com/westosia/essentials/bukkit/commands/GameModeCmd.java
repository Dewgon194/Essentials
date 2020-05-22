package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/*
    Author: Jamie Cee
    Description: This command allows a player to change their gamemode and allows admins with permissions to change gamemode
    of other players that they enter.

 */

@CommandAlias("gm")
@CommandPermission("essentials.command.gamemode")
public class GameModeCmd extends BaseCommand {

    @Default
    @Subcommand("c")
    @CommandCompletion("@players")
    @Description("Change player gamemode to creative")
    public void changeGMCreative(Player player, String[] args) {

        if (args.length == 0) {
            if (player.hasPermission("essentials.command.gamemode")) {
                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You are already in creative mode");
                } else {
                    player.setGameMode(GameMode.CREATIVE);
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "GameMode changed to creative");
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

    @Subcommand("s")
    @CommandCompletion("@players")
    @Description("Change player gamemode to survival")
    public void changeGMSurvival(Player player, String[] args) {

        if (args.length == 0) {
            if (player.hasPermission("essentials.command.gamemode")) {
                if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You are already in survival mode");
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "GameMode changed to survival");
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

    @Subcommand("sp")
    @CommandCompletion("@players")
    @Description("Change player gamemode to spectator")
    public void changeGMSpectator(Player player, String[] args) {

        if (args.length == 0) {
            if (player.hasPermission("essentials.command.gamemode")) {
                if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You are already in spectator mode");
                } else {
                    player.setGameMode(GameMode.SPECTATOR);
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "GameMode changed to spectator");
                }
            }
        } else if (player.hasPermission("essentials.command.gamemode.admin")) {
            if (Bukkit.getPlayer(args[0]) != null) {
                Player target = Bukkit.getPlayer(args[0]);
                if (!(target.getGameMode().equals(GameMode.SPECTATOR))) {
                    target.setGameMode(GameMode.SPECTATOR);
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, target.getName() + " is now in spectator");
                } else {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, target.getName() + " is already in spectator");
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Player does not exist");
            }
        }
    }

    @Subcommand("a")
    @CommandCompletion("@players")
    @Description("Change player gamemode to adventure")
    public void changeGMAdventure(Player player, String[] args) {

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

