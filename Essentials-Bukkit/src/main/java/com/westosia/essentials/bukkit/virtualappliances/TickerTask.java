package com.westosia.essentials.bukkit.virtualappliances;

import com.westosia.essentials.bukkit.Main;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TickerTask extends BukkitRunnable {

    public TickerTask() {
        this.runTaskTimer(Main.getInstance(), 0, 10);
    }

    @Override
    public void run() {
        for (PlayerAppliances pa : PlayerAppliances.getAllAppliances()) {
            if (pa.getFurnace() != null) pa.getFurnace().tick();
        }
    }
}
