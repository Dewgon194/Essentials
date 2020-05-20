package com.westosia.essentials.bungee;

import com.westosia.essentials.bungee.listeners.HomeListener;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

    private static Main instance;

    public String setHomesChannel = "sethomes";

    public void onEnable() {
        instance = this;
        getProxy().registerChannel(setHomesChannel);
        getProxy().getPluginManager().registerListener(this, new HomeListener());
    }

    public static Main getInstance() {
        return instance;
    }
}
