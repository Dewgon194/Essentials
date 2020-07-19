package com.westosia.essentials.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private long sessionTime;
    private long totalTime;

    private static Map<UUID, PlayerData> allData = new HashMap<>();

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        sessionTime = 0;
        totalTime = 0;
    }

    public PlayerData(UUID uuid, long totalTime) {
        this.uuid = uuid;
        sessionTime = 0;
        this.totalTime = totalTime;
    }

    public long getSessionTime() {
        return sessionTime;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setSessionTime(long sessionTime) {
        this.sessionTime = sessionTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public void cache() {
        allData.put(uuid, this);
    }

    public void uncache() {
        allData.remove(uuid);
    }

    public static PlayerData getData(UUID uuid) {
        return allData.get(uuid);
    }
}
