package com.westosia.essentials.bukkit;

import co.aikar.commands.PaperCommandManager;
import com.westosia.essentials.bukkit.commands.FeedPlayer;
import com.westosia.essentials.bukkit.commands.HealPlayer;
import com.westosia.essentials.bukkit.commands.Smite;
import com.westosia.essentials.core.homes.commands.SetHomeCmd;
import com.westosia.essentials.redis.SetHomeListener;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Text;
import net.md_5.bungee.protocol.packet.Commands;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {
    public final String sethomeChannel = "sethome";
    private static Main instance;

    public void onEnable() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new HealPlayer());
        manager.registerCommand(new FeedPlayer());
        manager.registerCommand(new Smite());
        instance = this;
        RedisConnector.getInstance().listenForChannel(sethomeChannel, new SetHomeListener());
        SetHomeCmd setHomeCmd = new SetHomeCmd();
        getCommand(setHomeCmd.sethome).setExecutor(setHomeCmd);

        getServer().getConsoleSender().sendMessage(Text.colour("&aEssentials enabled!"));
    }

    public static Main getInstance() {
        return instance;
    }
}
