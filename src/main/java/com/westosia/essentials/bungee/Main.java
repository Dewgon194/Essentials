package com.westosia.essentials.bungee;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BungeeCommandManager;
import com.westosia.essentials.bungee.commands.TeleportCmd;
import com.westosia.essentials.bungee.listeners.PostLoginListener;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

    private static Main instance;

    public void onEnable() {
        instance = this;

        registerCommands(new TeleportCmd());

        getProxy().getPluginManager().registerListener(this, new PostLoginListener());
    }

    private void registerCommands(BaseCommand... commands) {
        BungeeCommandManager manager = new BungeeCommandManager(this);
        for (BaseCommand command : commands) {
            manager.registerCommand(command);
        }
    }

    public static Main getInstance() {
        return instance;
    }
}
