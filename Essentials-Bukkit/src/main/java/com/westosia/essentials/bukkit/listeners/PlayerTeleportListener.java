package com.westosia.essentials.bukkit.listeners;

import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.homes.back.BackManager;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.westosiaapi.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("essentials.command.back")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    List<Home> addToBack = new ArrayList<>();
                    // Changing servers; only put in where they are going, if they are teleporting to a location
                    if (ServerChange.isChangingServers(player.getUniqueId())) {
                        ServerChange serverChange = ServerChange.getServerChange(player.getUniqueId());
                        if (serverChange.getReason().name().contains("TELEPORT")) {
                            Home backHome = HomeManager.fromString(serverChange.readInfo());
                            // Put info back so the join listener can still read it
                            serverChange.addRedisInfo(backHome.toString());
                            List<Home> backHomes = BackManager.getBackHomes(player.getUniqueId());
                            for (Home home : backHomes) {
                                if (home.getLocation().equals(backHome.getLocation())) {
                                    return;
                                }
                            }
                            addToBack.add(backHome);
                        }
                    } else {
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
                        addToBack.add(BackManager.createBackHome(event.getPlayer().getUniqueId(), event.getFrom()));
                        addToBack.add(BackManager.createBackHome(event.getPlayer().getUniqueId(), event.getTo()));
                    }
                    // Tell Redis about the new backHomes
                    for (int i = 0; i < addToBack.size(); i++) {
                        int finalI = i;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_BACKHOME, addToBack.get(finalI).toString()), i * 2);
                    }
                }
            }, 10);
        }
    }
}
