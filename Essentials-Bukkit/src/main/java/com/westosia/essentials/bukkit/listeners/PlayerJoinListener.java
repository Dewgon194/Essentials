package com.westosia.essentials.bukkit.listeners;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Get server name if it didn't get it on enable
        if (Main.getInstance().serverName.isEmpty()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> Main.getInstance().queryServerName());
        }
        // Tell Redis that this player joined a server, so that they don't get marked as logged off the network
        // and their homes uncached
        UUID uuid = event.getPlayer().getUniqueId();
        if (ServerChange.isChangingServers(uuid)) {
            ServerChange serverChange = ServerChange.getServerChange(uuid);
            if (serverChange.getToServer().isEmpty()) {
                serverChange.setToServer(Main.getInstance().serverName);
            }
            serverChange.setComplete(true);
            RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.toString());
        }
        //Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> RedisConnector.getInstance().getConnection().set("homes." + uuid.toString() + ".changing-servers", "false"));
        if (DatabaseEditor.getNick(uuid) != null) {
            event.getPlayer().setDisplayName(DatabaseEditor.getNick(uuid));
        }
    }
}
