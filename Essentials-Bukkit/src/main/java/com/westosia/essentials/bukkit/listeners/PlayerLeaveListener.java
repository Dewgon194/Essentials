package com.westosia.essentials.bukkit.listeners;

import com.westosia.cooldownapi.storage.Cooldown;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null); // Suppress Bukkit leave message

        // Tell Redis that this player is changing servers
        // If they join another server, Redis will report back and remove their changing status
        // If they don't, Redis will hear nothing. Check back in 5 seconds, if Redis hasn't removed changing status,
        // assume they are offline and uncache their homes
        UUID uuid = event.getPlayer().getUniqueId();
        if (!ServerChange.isChangingServers(uuid)) {
            ServerChange serverChange = new ServerChange(uuid, ServerChange.Reason.VOLUNTARY, Main.getInstance().SERVER_NAME);
            RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.toString());
        }
        PowerToolManager.uncacheAll(uuid);
        Cooldown.uncacheAll(uuid);
        Location lastLoc = event.getPlayer().getLocation();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> DatabaseEditor.setLastLocation(uuid, LocationStrings.friendlyLoc(lastLoc, true)));
    }
}
