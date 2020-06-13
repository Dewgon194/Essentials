package com.westosia.essentials.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.bukkit.Main;
import org.bukkit.Bukkit;

import java.util.*;

public class ServerChange {

    private UUID uuid;
    private Reason reason;
    private String fromServer, toServer;
    private boolean isComplete;
    private List<String> redisInfo;

    private static Map<UUID, ServerChange> serverChanges = new HashMap<>();

    public ServerChange(UUID uuid, Reason reason, String fromServer) {
        this.uuid = uuid;
        this.reason = reason;
        this.fromServer = fromServer;
        toServer = "";
        isComplete = false;
        redisInfo = new ArrayList<>();
    }

    public ServerChange(UUID uuid, Reason reason, String fromServer, String toServer) {
        this.uuid = uuid;
        this.reason = reason;
        this.fromServer = fromServer;
        this.toServer = toServer;
        isComplete = false;
        redisInfo = new ArrayList<>();
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

    public void addRedisInfo(String info) {
        redisInfo.add(info);
    }

    public void removeRedisInfo(String info) {
        redisInfo.remove(info);
    }

    public String readInfo() {
        if (redisInfo.size() > 0) {
            return redisInfo.remove(0);
        }
        return null;
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
        String serverChangeString = uuid.toString() + "|" +
                reason.name() + "|" +
                fromServer + "|" +
                toServer + "|" +
                isComplete;
        if (redisInfo.size() > 0) {
            StringBuilder infoString = new StringBuilder();
            redisInfo.forEach((info) -> {
                infoString.append("{")
                        .append(info)
                        .append("}");
            });
            String info = infoString.toString();
            serverChangeString = serverChangeString + "|" + info;
        }
        return serverChangeString;
    }

    public static ServerChange fromString(String serverChangeString) {
        // Split everything that is the server change string, but leave information whole
        String[] split = serverChangeString.split("\\|", 6);
        ServerChange serverChange = new ServerChange(UUID.fromString(split[0]), Reason.valueOf(split[1]), split[2], split[3]);
        serverChange.setComplete(Boolean.parseBoolean(split[4]));
        if (split.length > 5) {
            String infoStrings = split[5];
            // Split up each section of information
            String[] infoSplit = infoStrings.split("[{}]+");
            for (String info : infoSplit) {
                if (!info.isEmpty()) {
                    serverChange.addRedisInfo(info);
                }
            }
        }
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
