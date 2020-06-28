package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.bukkit.Main;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.entity.Player;

@CommandAlias("setspawn")
@CommandPermission("essentials.command.setspawn")
public class SetSpawnCmd extends BaseCommand {

    @Default
    @CommandCompletion("first")
    @Description("Set the world's spawn")
    public void setspawn(Player player, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("first")) {
            Main.getInstance().FIRST_SPAWN_LOC = player.getLocation();
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "You have set the world spawn point for new players.");
        } else {
            Main.getInstance().SPAWN_LOC = player.getLocation();
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "You have set the world spawn point.");
        }
    }
}
