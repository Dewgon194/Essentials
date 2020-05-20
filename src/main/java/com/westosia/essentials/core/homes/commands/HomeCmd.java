package com.westosia.essentials.core.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.essentials.core.homes.Home;
import com.westosia.essentials.core.homes.HomeManager;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.entity.Player;

@CommandAlias("home")
@CommandPermission("essentials.command.home")
public class HomeCmd extends BaseCommand {

    @Default
    @Description("Allows a player to teleport to their set homes")
    public void home(Player player, String[] args) {
        String homeName = "home";
        if (args.length > 0) {
            homeName = args[0];
        }
        Home home = HomeManager.getHome(player, homeName);
        if (home != null) {
            home.use();
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Teleported to home &f" + args[0]);
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The home &f" + homeName + "&adoes not exist");
        }
    }
}
