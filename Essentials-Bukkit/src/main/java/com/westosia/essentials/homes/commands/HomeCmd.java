package com.westosia.essentials.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.entity.Player;

@CommandAlias("home")
@CommandPermission("essentials.command.home")
public class HomeCmd extends BaseCommand {

    @Default
    @Description("Allows a player to teleport to their set homes")
    @CommandCompletion("@homes @nothing")
    public void home(Player player, String[] args) {
        String homeName = "home";
        if (args.length > 0) {
            homeName = args[0];
        }
        Home home = HomeManager.getHome(player, homeName);
        if (home != null) {
            if (!home.getServerName().equalsIgnoreCase(Main.getInstance().SERVER_NAME)) {
                ServerChange serverChange = new ServerChange(player.getUniqueId(), ServerChange.Reason.HOME_TELEPORT, Main.getInstance().SERVER_NAME, home.getServerName());
                serverChange.addRedisInfo(home.toString());
                serverChange.cache();
                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.toString());
                serverChange.send();
            } else {
                home.use();
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Teleported to home &f" + home.getName());
            }
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The home &f" + homeName + " &cdoes not exist");
        }
    }
}
