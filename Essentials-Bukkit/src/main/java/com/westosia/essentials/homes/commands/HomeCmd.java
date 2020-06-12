package com.westosia.essentials.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
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
            //Logger.info("home name: " + home.getServerName());
            //Logger.info("server name: " + Main.getInstance().serverName);
            if (!home.getServerName().equalsIgnoreCase(Main.getInstance().serverName)) {
                ServerChange serverChange = new ServerChange(player.getUniqueId(), ServerChange.Reason.HOME_TELEPORT, Main.getInstance().serverName, home.getServerName());
                //RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, "sent server change");
                serverChange.addRedisInfo(home.toString());
                serverChange.cache();
                //Logger.info(serverChange.toString());
                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.toString());
                serverChange.send();
            } else {
                home.use();
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Teleported to home &f" + home.getName());
            }
            // Wait 2 ticks in case player was sent to another server
            //Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> HomeManager.sendHomeData(home, player), 2);
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The home &f" + homeName + " &cdoes not exist");
        }
    }
}
