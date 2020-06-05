package com.westosia.essentials.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TeleportRequest {
    // Maps the receiver of a teleport request to the teleport request
    private static Map<ProxiedPlayer, TeleportRequest> requests = new HashMap<>();

    private ProxiedPlayer sender, receiver, target;
    private int timerID = -1;

    public TeleportRequest(ProxiedPlayer sender, ProxiedPlayer receiver, ProxiedPlayer target) {
        this.sender = sender;
        this.receiver = receiver;
        this.target = target;
        startExpirationTimer();

        requests.put(receiver, this);
    }

    public ProxiedPlayer getReceiver() {
        return receiver;
    }

    public ProxiedPlayer getSender() {
        return sender;
    }

    public ProxiedPlayer getTarget() {
        return target;
    }

    public void use(boolean accepted) {
        stopExpirationTimer();
        if (accepted) {
            int waitTime = 0;
            if (changedServers()) {
                waitTime = 100;
            }
            ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), this::notifyBukkit, waitTime, TimeUnit.MILLISECONDS);
        }
        requests.remove(getReceiver());
    }

    private boolean changedServers() {
        boolean changedServers = false;
        if (getWhoTeleports().getServer().getInfo() != getTarget().getServer().getInfo()) {
            changedServers = true;
            getWhoTeleports().connect(getTarget().getServer().getInfo());
        }
        return changedServers;
    }

    public ProxiedPlayer getWhoTeleports() {
        ProxiedPlayer teleporting = getReceiver();
        if (teleporting.equals(getTarget())) {
            teleporting = getSender();
        }
        return teleporting;
    }

    private void notifyBukkit() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("EssentialsTP");
        out.writeUTF(getWhoTeleports().getName());
        out.writeUTF(getTarget().getName());

        getTarget().getServer().getInfo().sendData("BungeeCord", out.toByteArray());
    }

    public static TeleportRequest getActiveTeleportRequest(ProxiedPlayer receiver) {
        return requests.get(receiver);
    }

    public static void removeRequest(ProxiedPlayer receiver) {
        TeleportRequest request = requests.remove(receiver);
        if (request != null && request.timerID > -1) {
            request.stopExpirationTimer();
        }
    }

    public static Set<ProxiedPlayer> cancelRequest(ProxiedPlayer sender) {
        Set<ProxiedPlayer> receivers = requests.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getSender().equals(sender))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        receivers.forEach(TeleportRequest::removeRequest);
        return receivers;
    }

    public void startExpirationTimer() {
        final TeleportRequest thisRequest = this;
        timerID = ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                timerID = -1;
                TeleportRequest.removeRequest(thisRequest.getReceiver());
            }
        }, 2, TimeUnit.MINUTES).getId();
    }

    public void stopExpirationTimer() {
        ProxyServer.getInstance().getScheduler().cancel(timerID);
        timerID = -1;
    }
}
