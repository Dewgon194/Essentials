package com.westosia.essentials.utils.teleports;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class Teleport {

    private ProxiedPlayer whosTPing;
    private TeleportTarget<?> target;

    public Teleport(ProxiedPlayer whosTPing, TeleportTarget<?> target) {
        this.whosTPing = whosTPing;
        this.target = target;
    }

    public ProxiedPlayer getWhosTPing() {
        return whosTPing;
    }

    public TeleportTarget<?> getTarget() {
        return target;
    }

    public void use() {
        int waitTime = 0;
        if (changedServers()) {
            waitTime = 100;
        }
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), this::notifyBukkit, waitTime, TimeUnit.MILLISECONDS);
    }

    private boolean changedServers() {
        boolean changedServers = false;
        if (getWhosTPing().getServer().getInfo() != getTarget().getServer()) {
            changedServers = true;
            getWhosTPing().connect(getTarget().getServer());
        }
        return changedServers;
    }

    private void notifyBukkit() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("EssentialsTP");
        out.writeUTF(getWhosTPing().getName());
        out.writeUTF(getTarget().getBukkitData());

        getTarget().getServer().sendData("BungeeCord", out.toByteArray());
    }
}
