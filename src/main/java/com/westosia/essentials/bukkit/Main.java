package com.westosia.essentials.bukkit;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.westosia.essentials.bukkit.commands.FeedPlayer;
import com.westosia.essentials.bukkit.commands.HealPlayer;
import com.westosia.essentials.bukkit.commands.Smite;
import com.westosia.essentials.bukkit.listeners.PluginMessageReceiver;
import com.westosia.essentials.core.homes.commands.DelHomeCmd;
import com.westosia.essentials.core.homes.commands.HomeCmd;
import com.westosia.essentials.core.homes.commands.SetHomeCmd;
import com.westosia.essentials.redis.DelHomeListener;
import com.westosia.essentials.redis.SetHomeListener;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Text;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public final String sethomeChannel = "sethome";
    public final String delhomeChannel = "delhome";
    private static Main instance;

    public void onEnable() {
        instance = this;

        RedisConnector.getInstance().listenForChannel(sethomeChannel, new SetHomeListener());
        RedisConnector.getInstance().listenForChannel(delhomeChannel, new DelHomeListener());

        registerCommands(
                new HealPlayer(),
                new FeedPlayer(),
                new Smite(),
                new SetHomeCmd(),
                new HomeCmd(),
                new DelHomeCmd()
        );

        // register bungee plugin channel
        getServer().getMessenger().registerIncomingPluginChannel( this, "BungeeCord", new PluginMessageReceiver());

        getServer().getConsoleSender().sendMessage(Text.colour("&aEssentials enabled!"));
    }

    private void registerCommands(BaseCommand... commands) {
        PaperCommandManager manager = new PaperCommandManager(this);
        for (BaseCommand command : commands) {
            manager.registerCommand(command);
        }
    }

    public static Main getInstance() {
        return instance;
    }
}
