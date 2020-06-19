package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("nick")
@CommandPermission("essentials.command.nick")
public class NickCmd extends BaseCommand {

    @Default
    @Description("Sets the players nickname for Chat")
    public void nickOther(Player player, String[] args) {
        if (args.length == 0) {
            player.setDisplayName(player.getName());
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Nickname Cleared");
            DatabaseEditor.removeNick(player.getUniqueId());
        } else if (args.length == 1) {
            if (Bukkit.getPlayer(args[0]) == null) {
                player.setDisplayName(args[0]);
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Your nickname has been set");
                DatabaseEditor.saveNick(args[0], player.getUniqueId());
            } else if (Bukkit.getPlayer(args[0]) != player) {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Too few arguments, Correct usage is /nick other <Player> <Nickname>");
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You cant nickname yourself this way");
            }
        } else if (args.length == 2) {
            if (Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]) != player) {
                if (args[1].equals("clear")) {
                    Player nicked = Bukkit.getPlayer(args[0]);
                    nicked.setDisplayName(nicked.getName());
                    WestosiaAPI.getNotifier().sendChatMessage(nicked, Notifier.NotifyStatus.SUCCESS, "Your nickname has been cleared by " + player.getName());
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Nickname cleared");
                    DatabaseEditor.removeNick(nicked.getUniqueId());

                } else {
                    Player nicked = Bukkit.getPlayer(args[0]);
                    nicked.setDisplayName(args[1]);
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, args[0] + "'s nickname has been set");
                    WestosiaAPI.getNotifier().sendChatMessage(nicked, Notifier.NotifyStatus.SUCCESS, "Your nickname has been set to " + args[1] + ", by " + player.getName());
                    DatabaseEditor.saveNick(args[1], nicked.getUniqueId());
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Too many arguments, Correct usage is /nick other <Player> <Nickname>");

            }


        } else if (args.length > 2) {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Too many arguments, Correct usage is /nick other <Player> <Nickname>");

        }
    }

}