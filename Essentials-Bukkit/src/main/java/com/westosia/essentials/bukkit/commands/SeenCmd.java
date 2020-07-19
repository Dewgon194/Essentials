package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.utils.PlayerData;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("seen")
@CommandPermission("essentials.command.seen")
public class SeenCmd extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Show when and where a player was last seen on the server")
    public void seen(Player player, String[] args) {
        if (args.length > 0) {
            UUID uuid = Bukkit.getPlayerUniqueId(args[0]);
            if (uuid != null) {
                PlayerData playerData = PlayerData.getData(uuid);
                long time = 0;
                if (playerData != null) {
                    time = playerData.getTotalTime();
                }
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "total time: " + time);
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "&f " + args[0] + "&c doesn't exist!");
            }
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Please specify a player.");
        }
    }
}
