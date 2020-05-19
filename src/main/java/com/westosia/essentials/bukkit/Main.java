package com.westosia.essentials.bukkit;

import com.westosia.essentials.core.homes.commands.SetHomeCmd;
import com.westosia.essentials.redis.SetHomeListener;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Text;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public final String sethomeChannel = "sethome";
    private static Main instance;

    public void onEnable() {
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
