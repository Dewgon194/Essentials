package com.westosia.essentials.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.bukkit.Main;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerChange {

    private UUID uuid;
    private Reason reason;
    private String fromServer, toServer;
    private boolean isComplete;

    private static Map<UUID, ServerChange> serverChanges = new HashMap<>();

    public ServerChange(UUID uuid, Reason reason, String fromServer) {
        this.uuid = uuid;
        this.reason = reason;
        this.fromServer = fromServer;
        toServer = "";
        isComplete = false;
    }

    public ServerChange(UUID uuid, Reason reason, String fromServer, String toServer) {
        this.uuid = uuid;
        this.reason = reason;
        this.fromServer = fromServer;
        this.toServer = toServer;
        isComplete = false;
    }

    public UUID getWhosChanging() {
        return uuid;
    }

    public String getFromServer() {
        return fromServer;
    }

    public String getToServer() {
        return toServer;
    }

    public void setToServer(String toServer) {
        this.toServer = toServer;
    }

    public Reason getReason() {
        return reason;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public void cache() {
        serverChanges.put(uuid, this);
    }

    public void uncache() {
        serverChanges.remove(uuid);
    }

    public void send() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getToServer());
        Bukkit.getPlayer(uuid).sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }

    @Override
    public String toString() {
        return uuid.toString() + "|" +
                reason.name() + "|" +
                fromServer + "|" +
                toServer + "|" +
                isComplete;
    }

    public static ServerChange fromString(String serverChangeString) {
        String[] split = serverChangeString.split("\\|");
        ServerChange serverChange = new ServerChange(UUID.fromString(split[0]), Reason.valueOf(split[1]), split[2], split[3]);
        serverChange.setComplete(Boolean.parseBoolean(split[4]));
        return serverChange;
    }

    public static boolean isChangingServers(UUID uuid) {
        return serverChanges.containsKey(uuid) && !getServerChange(uuid).isComplete();
    }

    public static ServerChange getServerChange(UUID uuid) {
        return serverChanges.get(uuid);
    }

    public enum Reason {
        VOLUNTARY(0), SERVER_DOWN(1), HOME_TELEPORT(2), TELEPORT_REQUEST(3);
        private int reason;

        Reason(int reason) {
            this.reason = reason;
        }

        int getCode() {
            return reason;
        }
    }
}
