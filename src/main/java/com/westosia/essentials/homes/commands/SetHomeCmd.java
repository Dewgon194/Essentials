package com.westosia.essentials.homes.commands;

import com.westosia.essentials.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.redisapi.redis.RedisConnector;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCmd implements CommandExecutor {

    public String sethome = "sethome";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Home home = new Home(player, "optesthome");
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().sethomeChannel, home.toString()));
        }
        return false;
    }
}
