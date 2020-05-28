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
import com.westosia.essentials.bukkit.listeners.PlayerJoinListener;
import com.westosia.essentials.bukkit.listeners.PlayerLeaveListener;
import com.westosia.essentials.bukkit.listeners.PluginMessageReceiver;
import com.westosia.essentials.core.homes.Home;
import com.westosia.essentials.core.homes.HomeManager;
import com.westosia.essentials.core.homes.commands.DelHomeCmd;
import com.westosia.essentials.core.homes.commands.HomeCmd;
import com.westosia.essentials.core.homes.commands.SetHomeCmd;
import com.westosia.essentials.redis.ChangeServerListener;
import com.westosia.essentials.redis.DelHomeListener;
import com.westosia.essentials.redis.ServerChangeInfo;
import com.westosia.essentials.redis.SetHomeListener;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.ServerChangeHelper;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Logger;
import com.westosia.westosiaapi.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.westosia.essentials.core.homes.HomeManager.getHomes;

public class Main extends JavaPlugin {
    public final String SET_HOME_REDIS_CHANNEL = "sethome";
    public final String DEL_HOME_REDIS_CHANNEL = "delhome";
    public final String CHANGE_SERVER_REDIS_CHANNEL = "changeserver";
    public String serverName = "";
    private static Main instance;

    public void onEnable() {
        instance = this;
        checkDB();
        RedisConnector.getInstance().listenForChannel(SET_HOME_REDIS_CHANNEL, new SetHomeListener());
        RedisConnector.getInstance().listenForChannel(DEL_HOME_REDIS_CHANNEL, new DelHomeListener());
        RedisConnector.getInstance().listenForChannel(CHANGE_SERVER_REDIS_CHANNEL, new ChangeServerListener());

        registerEvents(
                new PlayerLeaveListener(),
                new PlayerJoinListener()
                );

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

    //todo: fix task cuz now it doesnt work when it did earlier
    // also instead of going through online, go through still cached homes
    public void onDisable() {
        // Save to database on disable
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.execute(() -> onlinePlayers.forEach((player) -> {
            ServerChangeHelper.saveHomesToDB(player.getUniqueId().toString());
            ServerChangeInfo.tellRedis(player.getUniqueId().toString(), "true", "db");
        }));
        service.shutdown();
    }

    private void registerCommands(BaseCommand... commands) {
        PaperCommandManager manager = new PaperCommandManager(this);
        for (BaseCommand command : commands) {
            manager.registerCommand(command);
        }
    }

    private void registerEvents(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public void queryServerName() {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("GetServer");
        getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", output.toByteArray());
    }

    private void checkDB() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            boolean exists = DatabaseEditor.checkIfTableExists();
            if (!exists) {
                DatabaseEditor.createTable();
            }
        });
    }
}
