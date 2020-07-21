package com.westosia.essentials.utils;

import com.westosia.essentials.bukkit.Main;
import com.westosia.redisapi.redis.RedisConnector;
import org.bukkit.Bukkit;

public class RedisAnnouncer {

    public static void tellRedis(Channel channel, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> RedisConnector.getInstance().getConnection().publish(channel.getChannel(), message));
    }

    public enum Channel {
        SET_HOME("sethome"), DEL_HOME("delhome"), CHANGE_SERVER("changeserver"), QUERY_HOMES("queryhomes"), SUDO("sudo");

        private final String channel;

        Channel(String channel) {
            this.channel = channel;
        }

        public String getChannel() {
            return channel;
        }
    }
}
