package com.westosia.essentials.core.homes.commands;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.core.homes.Home;
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
