package com.westosia.essentials.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.entity.Player;

@CommandAlias("delhome")
@CommandPermission("essentials.command.delhome")
public class DelHomeCmd extends BaseCommand {

    @Default
    @Description("Deletes the home of the given name")
    public void delHome(Player player, String[] args) {
        if (args.length > 0) {
            Home home = HomeManager.getHome(player, args[0]);
            if (home != null) {
                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.DEL_HOME, home.toString());
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Home &f" + args[0] + "&a removed");
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Home &f" + args[0] + "&c not found");
            }
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "No home specified!");
        }
    }
}
