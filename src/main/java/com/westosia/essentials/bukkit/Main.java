package com.westosia.essentials.bukkit;

import co.aikar.commands.PaperCommandManager;
import com.westosia.essentials.bukkit.commands.FeedPlayer;
import com.westosia.essentials.bukkit.commands.HealPlayer;
import com.westosia.essentials.core.homes.commands.DelHomeCmd;
import com.westosia.essentials.core.homes.commands.HomeCmd;
import com.westosia.essentials.core.homes.commands.SetHomeCmd;
import com.westosia.essentials.redis.DelHomeListener;
import com.westosia.essentials.redis.SetHomeListener;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Text;
import net.md_5.bungee.protocol.packet.Commands;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {
    public final String sethomeChannel = "sethome";
    public final String delhomeChannel = "delhome";
    private static Main instance;

    public void onEnable() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new HealPlayer());
        manager.registerCommand(new FeedPlayer());
        manager.registerCommand(new SetHomeCmd());
        manager.registerCommand(new HomeCmd());
        manager.registerCommand(new DelHomeCmd());
        instance = this;
        RedisConnector.getInstance().listenForChannel(sethomeChannel, new SetHomeListener());
        RedisConnector.getInstance().listenForChannel(delhomeChannel, new DelHomeListener());

        getServer().getConsoleSender().sendMessage(Text.colour("&aEssentials enabled!"));
    }

    public static Main getInstance() {
        return instance;
    }
}
