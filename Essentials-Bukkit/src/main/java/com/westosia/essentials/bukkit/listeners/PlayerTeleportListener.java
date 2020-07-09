package com.westosia.essentials.bukkit.listeners;

import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.homes.back.BackManager;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.westosiaapi.Main;
import com.westosia.westosiaapi.utils.Logger;
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
                            Logger.broadcast("changing servers for a teleport");
                            Home backHome = HomeManager.fromString(serverChange.readInfo());
                            // Put info back so the join listener can still read it
                            serverChange.addRedisInfo(backHome.toString());
                            List<Home> backHomes = BackManager.getBackHomes(player.getUniqueId());
                            for (Home home : backHomes) {
                                if (home.getLocation().equals(backHome.getLocation())) {
                                    Logger.broadcast("(back)home already logged");
                                    return;
                                }
                            }
                            addToBack.add(backHome);
                        }
                    } else {
                        Logger.broadcast("not changing servers");
                        // Not changing servers, record to and from locations
                        List<Home> backHomes = BackManager.getBackHomes(event.getPlayer().getUniqueId());
                        if (backHomes != null) {
                            for (Home backHome : backHomes) {
                                // Do not put in the list of backHomes if you are teleporting to a backHome
                                if (backHome.getLocation().equals(event.getTo())) {
                                    Logger.broadcast("not adding to backhomes");
                                    return;
                                }
                            }
                        }
                        addToBack.add(BackManager.createBackHome(event.getPlayer().getUniqueId(), event.getFrom()));
                        addToBack.add(BackManager.createBackHome(event.getPlayer().getUniqueId(), event.getTo()));
                    }
                    // Tell Redis about the new backHomes
                    Logger.broadcast("you teleported");
                    for (int i = 0; i < addToBack.size(); i++) {
                        int finalI = i;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_BACKHOME, addToBack.get(finalI).toString()), i * 2);
                    }
                }
            }, 8);
        }
        /*
        List<Home> addToBack = new ArrayList<>();
        if (ServerChange.isChangingServers(player.getUniqueId())) {
            ServerChange serverChange = ServerChange.getServerChange(player.getUniqueId());
            if (serverChange.getReason().name().contains("TELEPORT")) {
                Home backHome = HomeManager.fromString(serverChange.readInfo());
                addToBack.add(backHome);
            }
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                if (player.hasPermission("essentials.command.back")) {
                    List<Home> backHomes = BackManager.getBackHomes(event.getPlayer().getUniqueId());
                    if (backHomes != null) {
                        for (Home backHome : backHomes) {
                            // Do not put in the list of backHomes if you are teleporting to a backHome
                            if (backHome.getLocation().equals(event.getTo())) {
                                Logger.broadcast("not adding to backhomes");
                                //event.getPlayer().sendMessage("not adding to backhomes");
                                return;
                            }
                        }

                    }
                    Logger.broadcast("you teleported");
                    for (int i = 0; i < addToBack.size(); i++) {
                        int finalI = i;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_BACKHOME, addToBack.get(finalI).toString()), i);
                    }
                    //event.getPlayer().sendMessage("you teleported");
                    Home fromBack = BackManager.createBackHome(event.getPlayer().getUniqueId(), event.getFrom());
                    RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_BACKHOME, fromBack.toString());
                    // Wait a tick so the first one goes through Redis before this one (as opposed to the same exact time)
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                        Home toBack = BackManager.createBackHome(event.getPlayer().getUniqueId(), event.getTo());
                        RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_BACKHOME, toBack.toString());
                    }, 1);

                }
            }, 4);
        }
         */
    }
}
