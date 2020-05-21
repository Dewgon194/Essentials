package com.westosia.essentials.core.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.core.homes.Home;
import com.westosia.essentials.utils.LocationStrings;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@CommandAlias("sethome")
@CommandPermission("essentials.command.sethome")
public class SetHomeCmd extends BaseCommand {


    @Default
    @Description("Sets a home to the player's location")
    public void setHome(Player player, String[] args) {
        String homeName = "home";
        if (args.length > 0) {
            homeName = args[0];
        }
        Home home = new Home(player, homeName, Main.getInstance().serverName, player.getLocation());
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().SET_HOME_REDIS_CHANNEL, home.toString()));
        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Set home &f" + homeName + " &ato your location");
    }
}
