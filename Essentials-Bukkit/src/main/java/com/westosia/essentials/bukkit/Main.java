package com.westosia.essentials.bukkit;

import co.aikar.commands.*;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.bukkit.commands.*;
import com.westosia.essentials.bukkit.commands.gamemodes.GamemodeAdventureCmd;
import com.westosia.essentials.bukkit.commands.gamemodes.GamemodeCreativeCmd;
import com.westosia.essentials.bukkit.commands.gamemodes.GamemodeSpectatorCmd;
import com.westosia.essentials.bukkit.commands.gamemodes.GamemodeSurvivalCmd;
import com.westosia.essentials.bukkit.listeners.*;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.homes.back.BackCmd;
import com.westosia.essentials.homes.commands.DelHomeCmd;
import com.westosia.essentials.homes.commands.HomeCmd;
import com.westosia.essentials.homes.commands.HomesCmd;
import com.westosia.essentials.homes.commands.SetHomeCmd;
import com.westosia.essentials.redis.*;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.LocationStrings;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main extends JavaPlugin {
    public Location FIRST_SPAWN_LOC;
    public Location SPAWN_LOC;
    public String SERVER_NAME = "";
    private static Main instance;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        SPAWN_LOC = LocationStrings.fromConfig("spawn-location");
        FIRST_SPAWN_LOC = LocationStrings.fromConfig("first-spawn");
        checkDB();
        checkNickDB();
        checkSeenDB();
        checkPowerToolsDB();
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        // If people are on when enabled, load them in from db to prevent yeeting of homes on reload
        // This really shouldn't be done live anyways. Repeated reloads produces odd behaviour from the Redis listeners
        players.forEach((player) -> {
            Collection<Home> homes = DatabaseEditor.getHomesInDB(player.getUniqueId()).values();
            homes.forEach(HomeManager::cacheHome);
        });
        RedisConnector.getInstance().listenForChannel(RedisAnnouncer.Channel.SET_HOME.getChannel(), new SetHomeListener());
        RedisConnector.getInstance().listenForChannel(RedisAnnouncer.Channel.DEL_HOME.getChannel(), new DelHomeListener());
        RedisConnector.getInstance().listenForChannel(RedisAnnouncer.Channel.CHANGE_SERVER.getChannel(), new ChangeServerListener());
        RedisConnector.getInstance().listenForChannel(RedisAnnouncer.Channel.QUERY_HOMES.getChannel(), new QueryHomesListener());
        RedisConnector.getInstance().listenForChannel(RedisAnnouncer.Channel.SUDO.getChannel(), new SudoListener());
        RedisConnector.getInstance().listenForChannel(RedisAnnouncer.Channel.NICKNAME.getChannel(), new NicknameListener());
        RedisConnector.getInstance().listenForChannel(RedisAnnouncer.Channel.SET_BACKHOME.getChannel(), new SetBackhomeListener());
        registerEvents(
                new PlayerLeaveListener(),
                new PlayerJoinListener(),
                new PlayerTeleportListener(),
                new PlayerInteractListener(),
                new AccessoryListener(),
                new DivinityChangeListener()
        );

        registerCommands(
                new HealPlayerCmd(),
                new FeedPlayerCmd(),
                new SmiteCmd(),
                new SetHomeCmd(),
                new HomeCmd(),
                new DelHomeCmd(),
                new HomesCmd(),
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
                new RepairCmd(),
                new NickCmd(),
                new SpawnCmd(),
                new SetSpawnCmd(),
                new JoinKitCmd(),
                new InvseeCmd(),
                new SudoCmd(),
                new BackCmd(),
                new PowerToolCmd()
        );

        // register bungee plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessageReceiver());
        queryServerName();

        getServer().getConsoleSender().sendMessage(Text.colour("&aEssentials enabled!"));
    }

    public void onDisable() {
        getConfig().set("spawn-location", LocationStrings.toString(SPAWN_LOC));
        getConfig().set("first-spawn", LocationStrings.toString(FIRST_SPAWN_LOC));
        saveConfig();
        // Save to database on disable
        Set<UUID> cached = HomeManager.getCachedHomeOwners();

        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.execute(() -> cached.forEach((uuid) -> {
            DatabaseEditor.saveAllHomes(uuid);
            ServerChange serverChange = new ServerChange(uuid, ServerChange.Reason.SERVER_DOWN, SERVER_NAME);
            RedisConnector.getInstance().getConnection().publish(RedisAnnouncer.Channel.CHANGE_SERVER.getChannel(), serverChange.toString());
        }));
        service.shutdown();
    }

    private void registerCommands(BaseCommand... commands) {
        PaperCommandManager manager = new PaperCommandManager(this);
        for (BaseCommand command : commands) {
            manager.registerCommand(command);
        }
        manager.getCommandCompletions().registerCompletion("homes", context -> HomeManager.getHomes(context.getPlayer()).keySet());
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

    private void checkNickDB() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            boolean exists = DatabaseEditor.checkIfNickTableExists();
            if (!exists) {
                DatabaseEditor.createNickTable();
            }
        });
    }

    private void checkSeenDB() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            boolean exists = DatabaseEditor.checkIfSeenTableExists();
            if (!exists) {
                DatabaseEditor.createSeenTable();
            }
        });
    }

    private void checkPowerToolsDB() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            boolean exists = DatabaseEditor.checkIfPowerToolsTableExists();
            if (!exists) {
                DatabaseEditor.createPowerToolsTable();
            }
        });
    }
}
