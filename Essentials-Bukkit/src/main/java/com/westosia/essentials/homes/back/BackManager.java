package com.westosia.essentials.homes.back;

import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import redis.clients.jedis.Jedis;

import java.util.*;

public class BackManager {

    private static Map<UUID, List<Home>> backHomes = new HashMap<>();

    public static List<Home> getBackHomes(UUID uuid) {
        return backHomes.get(uuid);
    }

    public static void cacheBackHome(Home backHome) {
        List<Home> playerBackHomes = getBackHomes(backHome.getOwner().getUniqueId());
        if (playerBackHomes == null) {
            playerBackHomes = new ArrayList<>();
        } else {
            // Remove oldest backhome if they have 5 or more
            if (playerBackHomes.size() >= 5) {
                playerBackHomes.remove(0);
            }
        }
        playerBackHomes.add(backHome);
        backHomes.put(backHome.getOwner().getUniqueId(), playerBackHomes);
    }

    public static void setBackHome(Home backHome) {
        cacheBackHome(backHome);

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            Jedis jedis = RedisConnector.getInstance().getConnection();
            String key = backHome.getOwner().getUniqueId() + ".back";
            if (jedis.llen(key) >= 5) {
                jedis.rpop(key);
            }
            jedis.lpush(key, backHome.toString());
            Logger.info(jedis.lrange(key, 0, jedis.llen(key)).get(0));
        });
    }

    public static List<Home> getBackHomesFromRedis(UUID uuid) {
        Jedis jedis = RedisConnector.getInstance().getConnection();
        String key = uuid + ".back";
        List<String> homeStrings = jedis.lrange(key, 0, jedis.llen(key));
        List<Home> homes = new ArrayList<>();
        homeStrings.forEach(homeString -> homes.add(HomeManager.fromString(homeString)));
        return homes;
    }

    public static Home createBackHome(UUID uuid, Location location) {
        int homeNum = 0;
        List<Home> playerBackHomes = getBackHomes(uuid);
        if (playerBackHomes != null && playerBackHomes.size() > 0) {
            homeNum = playerBackHomes.size();
        }
        return new Home(uuid, "back" + homeNum, Main.getInstance().SERVER_NAME, location);
    }
}
