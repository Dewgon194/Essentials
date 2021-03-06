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

    private static final int MAX_BACK_HOMES = 6;

    private static Map<UUID, List<Home>> backHomes = new HashMap<>();
    private static Map<UUID, Integer> backIndex = new HashMap<>();

    public static List<Home> getBackHomes(UUID uuid) {
        return backHomes.get(uuid);
    }

    public static int getBackIndex(UUID uuid) {
        return backIndex.getOrDefault(uuid, 0);
    }

    public static void setBackIndex(UUID uuid, int index) {
        int finalIndex = cacheBackIndex(uuid, index);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> RedisConnector.getInstance().getConnection().set(uuid.toString() + ".backIndex", finalIndex + ""));
    }

    public static int getIndexFromRedis(UUID uuid) {
        String indexString = RedisConnector.getInstance().getConnection().get(uuid.toString() + "backIndex");
        if (indexString == null || indexString.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(indexString);
    }

    public static int cacheBackIndex(UUID uuid, int index) {
        int backHomesAmount = BackManager.getBackHomes(uuid).size();
        if (index >= backHomesAmount) {
            index = backHomesAmount - 1;
        }
        backIndex.put(uuid, index);
        return index;
    }

    public static void cacheBackHome(Home backHome) {
        List<Home> playerBackHomes = getBackHomes(backHome.getOwner().getUniqueId());
        if (playerBackHomes == null) {
            playerBackHomes = new ArrayList<>();
        } else {
            // Remove oldest backhome if they have 5 or more
            if (playerBackHomes.size() >= MAX_BACK_HOMES) {
                playerBackHomes.remove(0);
            }
        }
        playerBackHomes.add(getBackIndex(backHome.getOwner().getUniqueId()), backHome);
        backHomes.put(backHome.getOwner().getUniqueId(), playerBackHomes);
    }

    public static void setBackHome(Home backHome) {
        cacheBackHome(backHome);

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            Jedis jedis = RedisConnector.getInstance().getConnection();
            String key = backHome.getOwner().getUniqueId().toString() + ".back";
            if (jedis.llen(key) >= MAX_BACK_HOMES) {
                jedis.rpop(key);
            }
            jedis.lpush(key, backHome.toString());
            Logger.info(jedis.lrange(key, 0, jedis.llen(key)).get(0));
        });
    }

    public static List<Home> getBackHomesFromRedis(UUID uuid) {
        Jedis jedis = RedisConnector.getInstance().getConnection();
        String key = uuid.toString() + ".back";
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

    public static void removeEntry(UUID uuid) {
        backHomes.remove(uuid);
        backIndex.remove(uuid);
    }
}
