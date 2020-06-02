package com.westosia.essentials.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("homes")
@CommandPermission("essentials.command.homes")
public class HomesCmd extends BaseCommand {

    @Default
    @Description("Lets a player view theirs or others' homes")
    public void homes(Player player, String[] args) {
        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "did /homes");
/*
        if (home != null) {
            if (!home.getServerName().equalsIgnoreCase(Main.getInstance().serverName)) {
                sendToServer(player, home.getServerName());
            }
            // Wait 2 ticks in case player was sent to another server
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> sendHomeData(home), 2);
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The home &f" + homeName + " &cdoes not exist");
        }*/
    }

    @Subcommand("use")
    @CommandCompletion("@players")
    @Description("Allows a player to use another player's home")
    public void use(Player player, String[] args) {
        if (args.length >= 2) {
            UUID pUUID = Bukkit.getPlayerUniqueId(args[0]);
            String homeName = args[1];
            if (pUUID != null) {
                Home home = HomeManager.getHomes(pUUID).get(homeName);
                //TODO: make work with uncached players
                if (home != null) {
                    player.teleport(home.getLocation());
                }
            }
        }
        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "did /homes set");
/*
        if (home != null) {
            if (!home.getServerName().equalsIgnoreCase(Main.getInstance().serverName)) {
                sendToServer(player, home.getServerName());
            }
            // Wait 2 ticks in case player was sent to another server
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> sendHomeData(home), 2);
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The home &f" + homeName + " &cdoes not exist");
        }*/
    }

    @Subcommand("set")
    @CommandCompletion("@players")
    @Description("Allows a player to set a home for another player")
    public void set(Player player, String[] args) {
        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "did /homes set");
/*
        if (home != null) {
            if (!home.getServerName().equalsIgnoreCase(Main.getInstance().serverName)) {
                sendToServer(player, home.getServerName());
            }
            // Wait 2 ticks in case player was sent to another server
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> sendHomeData(home), 2);
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The home &f" + homeName + " &cdoes not exist");
        }*/
    }
}
