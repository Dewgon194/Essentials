package com.westosia.essentials.bukkit;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.bukkit.commands.*;
import com.westosia.essentials.bukkit.commands.gamemodes.GamemodeAdventureCmd;
import com.westosia.essentials.bukkit.commands.gamemodes.GamemodeCreativeCmd;
import com.westosia.essentials.bukkit.commands.gamemodes.GamemodeSpectatorCmd;
import com.westosia.essentials.bukkit.commands.gamemodes.GamemodeSurvivalCmd;
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
    public final String SET_HOME_REDIS_CHANNEL = "sethome";
    public final String DEL_HOME_REDIS_CHANNEL = "delhome";
    public String serverName = "";
    private static Main instance;

    public void onEnable() {
        instance = this;
        RedisConnector.getInstance().listenForChannel(SET_HOME_REDIS_CHANNEL, new SetHomeListener());
        RedisConnector.getInstance().listenForChannel(DEL_HOME_REDIS_CHANNEL, new DelHomeListener());

        registerCommands(
                new HealPlayerCmd(),
                new FeedPlayerCmd(),
                new SmiteCmd(),
                new SetHomeCmd(),
                new HomeCmd(),
                new DelHomeCmd(),
                new BurnCmd(),
                new CraftCmd(),
                new FireballCmd(),
                new HatCmd(),
                new GamemodeAdventureCmd(),
                new GamemodeCreativeCmd(),
                new GamemodeSpectatorCmd(),
                new GamemodeSurvivalCmd(),
                new FusCmd(),
                new JumpCmd(),
                new RepairCmd()
        );

        // register bungee plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel( this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel( this, "BungeeCord", new PluginMessageReceiver());
        queryServerName();

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

    private void queryServerName() {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("GetServer");
        getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", output.toByteArray());
    }
}
