package com.westosia.essentials;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BungeeCommandManager;
import com.westosia.essentials.commands.*;
import com.westosia.essentials.listeners.PlayerDisconnectListener;
import com.westosia.essentials.listeners.PostLoginListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class Main extends Plugin {

    private static Main instance;

    public static LuckPerms LUCK_PERMS;

    public void onEnable() {
        instance = this;

        LUCK_PERMS = LuckPermsProvider.get();

        registerCommands(   new TeleportCmd(),
                            new TpaCmd(),
                            new TpaHereCmd(),
                            new TpAcceptCmd(),
                            new TpDenyCmd(),
                            new TpCancelCmd());

        registerListeners(  new PostLoginListener(),
                            new PlayerDisconnectListener());
    }

    private void registerCommands(BaseCommand... commands) {
        BungeeCommandManager manager = new BungeeCommandManager(this);
        for (BaseCommand command : commands) {
            manager.registerCommand(command);
        }
    }

    private void registerListeners(Listener... listeners) {
        PluginManager manager = getProxy().getPluginManager();
        for (Listener listener : listeners) {
            manager.registerListener(this, listener);
        }
    }
    public static Main getInstance() {
        return instance;
    }
}
