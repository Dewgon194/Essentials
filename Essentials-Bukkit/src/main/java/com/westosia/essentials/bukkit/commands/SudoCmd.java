package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("sudo")
@CommandPermission("essentials.command.sudo")
public class SudoCmd extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Run a command or send a chat message on another player's behalf")
    public void sudo(Player player, String[] args) {
        if (args.length > 1) {
            UUID target = Bukkit.getPlayerUniqueId(args[0]);
            if (target != null) {
                StringBuilder cmd = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    cmd.append(args[i]);
                    if (i != args.length - 1) {
                        cmd.append(" ");
                    }
                }
                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SUDO, target.toString() + "|" + cmd.toString());
                if (cmd.toString().startsWith("c:")) {
                    String chatMsg = cmd.toString().substring(2);
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "You have made &f" + args[0] + "&a type in chat &f" + chatMsg);
                } else {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "You have made &f" + args[0] + "&a run the command &f" + cmd.toString());
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "&f" + args[0] + "&c does not exist!");
            }
        } else {
            if (args.length == 0) {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Please specify a player and a command.");
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Please give a command for the player to run.");
            }
        }
    }
}
