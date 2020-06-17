package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.bukkit.Main;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("spawn")
@CommandPermission("essentials.command.spawn")
public class SpawnCmd extends BaseCommand {

    @Default
    @Description("Go to the world's spawn")
    public void spawn(Player player, String[] args) {
        player.teleport(Main.getInstance().SPAWN_LOC);
        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "You have teleported to spawn.");
    }
}
