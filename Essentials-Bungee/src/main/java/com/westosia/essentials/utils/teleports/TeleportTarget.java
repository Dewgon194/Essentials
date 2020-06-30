package com.westosia.essentials.utils.teleports;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeleportTarget<T> {

    private T type;

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    public ServerInfo getServer() {
        if (getType() instanceof ProxiedPlayer) {
            return ((ProxiedPlayer) getType()).getServer().getInfo();
        } else {
            return ProxyServer.getInstance().getServerInfo(((Location) getType()).getServer());
        }
    }

    public String getBukkitData() {
        if (getType() instanceof ProxiedPlayer) {
            return ((ProxiedPlayer) getType()).getName();
        }
        return getType().toString();
    }
}
