package com.westosia.essentials.redis;

import com.westosia.essentials.bukkit.Main;
import com.westosia.redisapi.redis.RedisConnector;

public class ServerChangeInfo {

    public static boolean isChangingServers(String uuid) {
        String changingStatus = RedisConnector.getInstance().getConnection().get("homes." + uuid + ".changing-servers");
        changingStatus = changingStatus.split(":++")[0];
        return changingStatus.equalsIgnoreCase("true");
    }

    public static boolean savedToDbOnChange(String uuid) {
        String changingStatus = RedisConnector.getInstance().getConnection().get("homes." + uuid + ".changing-servers");
        changingStatus = changingStatus.split(":++")[1];
        return changingStatus.equalsIgnoreCase("db");
    }

    public static String disconnectedFrom(String uuid) {
        String changingStatus = RedisConnector.getInstance().getConnection().get("homes." + uuid + ".changing-servers");
        changingStatus = changingStatus.split(":++")[2];
        return changingStatus;
    }

    public static void tellRedis(String uuidString, String isChanging, String savedToDB) {
        RedisConnector.getInstance().getConnection().set("homes." + uuidString + ".changing-servers", isChanging + ":" + savedToDB + ":" + Main.getInstance().serverName);
        RedisConnector.getInstance().getConnection().publish(Main.getInstance().CHANGE_SERVER_REDIS_CHANNEL, uuidString);
    }
}
