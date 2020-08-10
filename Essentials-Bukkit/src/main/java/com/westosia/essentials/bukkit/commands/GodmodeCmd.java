package com.westosia.essentials.bukkit.commands;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

@CommandAlias("godmode")
@CommandPermission("essentials.command.godmode")
public class GodmodeCmd extends BaseCommand {

    @Default
    @Description("Prevents the user from taking damage/dying.")
    public void god(Player player, String[] args) {
        if (args.length == 0) {
            if (player.hasMetadata("GodmodeOn")) {
                player.removeMetadata("GodmodeOn", Main.getInstance());
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Godmode is now disabled!");
                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.GODMODE, player.getName() + "|" + "off");

            } else {
                player.setMetadata("GodmodeOn", new FixedMetadataValue(Main.getInstance(), "godmode"));
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Godmode is now enabled!");
                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.GODMODE, player.getName() + "|" + "on");

            }
        } else if (args.length == 1) {
            if (Bukkit.getPlayer(args[0]) != null) {
                Player player2 = Bukkit.getPlayer(args[0]);
                if (!player2.hasMetadata("GodmodeOn")) {
                    player2.setMetadata("GodmodeOn", new FixedMetadataValue(Main.getInstance(), "godmode"));
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, player2.getName() + "'s Godmode enabled!");
                    WestosiaAPI.getNotifier().sendChatMessage(player2, Notifier.NotifyStatus.SUCCESS, "Godmode is now enabled!");
                    RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.GODMODE, player2.getName() + "|" + "on");

                } else if (player2.hasMetadata("GodmodeOn")) {
                    player2.removeMetadata("GodmodeOn", Main.getInstance());
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, player2.getName() + "'s Godmode Disabled");
                    WestosiaAPI.getNotifier().sendChatMessage(player2, Notifier.NotifyStatus.ERROR, "Godmode is now disabled");
                    RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.GODMODE, player2.getName() + "|" + "off");

                } else {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Specified player is not online or does not exist!");
                }
            }
        } else if (args.length > 1) {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Too many arguments!");

        }


    }
}
