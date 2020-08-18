package com.westosia.essentials.bukkit.listeners;

import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.back.BackManager;
import com.westosia.essentials.utils.RedisAnnouncer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location location = event.getEntity().getLocation();
        if (player.hasPermission("essentials.command.back")) {
            // Record death location if it's not already there
            List<Home> backHomes = BackManager.getBackHomes(player.getUniqueId());
            if (backHomes != null) {
                for (Home backHome : backHomes) {
                    // Do not put in the list of backHomes if you are teleporting to a backHome
                    if (backHome.getLocation().equals(location)) {
                        return;
                    }
                }
            }
            Home backHome = BackManager.createBackHome(player.getUniqueId(), location);
            RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_BACKHOME, backHome.toString());
        }
        /*
        if (player.hasPermission("essentials.command.back")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                // Not changing servers, record to and from locations
                List<Home> backHomes = BackManager.getBackHomes(event.getPlayer().getUniqueId());
                if (backHomes != null) {
                    for (Home backHome : backHomes) {
                        // Do not put in the list of backHomes if you are teleporting to a backHome
                        if (backHome.getLocation().equals(event.getTo())) {
                            return;
                        }
                    }
                }
            }, 10);
        }*/
    }
}
