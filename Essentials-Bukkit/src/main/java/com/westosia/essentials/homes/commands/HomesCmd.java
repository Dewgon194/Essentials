package com.westosia.essentials.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.*;

@CommandAlias("homes")
@CommandPermission("essentials.command.homes")
public class HomesCmd extends BaseCommand {
    private static Map<UUID, Integer> unload = new HashMap<>();
    @Default
    @Subcommand("view")
    @Description("Lets a player view theirs or others' homes")
    public void homes(Player player, String[] args) {
        if (args.length < 1) {
            Map<String, Home> homes = HomeManager.getHomes(player);
            if (homes.size() < 1) {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You have no set homes");
            } else {
                String homeList = getHomesList(homes.keySet());
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Found homes " + homeList);
            }
        } else {
            if (player.hasPermission("essentials.command.homes.view.others")) {
                UUID uuid = Bukkit.getPlayerUniqueId(args[0]);
                if (uuid != null) {
                    Map<String, Home> homes;
                    if (!HomeManager.hasHomesLoaded(uuid)) {
                        // Load homes in
                        homes = loadHomes(uuid);
                        if (homes == null || homes.isEmpty()) {
                            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Could not retrieve homes for &f" + args[0]);
                            return;
                        }
                    } else {
                        homes = HomeManager.getHomes(uuid);
                    }
                    if (homes.size() > 0) {
                        String homeList = getHomesList(homes.keySet());
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Found homes " + homeList);
                    } else {
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "This player has no set homes");
                    }
                } else {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The player &f" + args[0] + " &cdoes not exist");
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You don't have permission to run that command");
            }
        }
    }

    private String getHomesList(Set<String> homes) {
        StringBuilder homeList = new StringBuilder();
        int index = 0;
        for (String home : homes) {
            homeList.append("&f");
            homeList.append(home);
            index++;
            if (index < homes.size()) {
                homeList.append("&a, ");
            }
        }
        return homeList.toString();
    }

    private void startUnloadTimer(UUID uuid) {
        // Unload homes after 2 minutes
        if (!unload.containsKey(uuid)) {
            int id = Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                unload.remove(uuid);
                if (HomeManager.getHomes(uuid) != null) {
                    DatabaseEditor.saveAllHomes(uuid);
                    // Ask other servers to uncache homes
                    RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.DEL_HOME, uuid.toString());
                }
            }, 2400).getTaskId();
            unload.put(uuid, id);
        }
    }

    private Map<String, Home> loadHomes(UUID uuid) {
        Map<String, Home> homes = null;
        FutureTask<Map<String, Home>> future = new FutureTask<>(() -> {
            // Get homes from database
            Map<String, Home> dbHomes = DatabaseEditor.getHomesInDB(uuid);
            if (dbHomes.size() > 0) {
                // Load them into Redis
                dbHomes.values().forEach(home -> RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_HOME, home.toString()));
            } else {
                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_HOME, uuid.toString());
            }
            return dbHomes;
        });
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.execute(future);
        try {
            homes = future.get(2, TimeUnit.SECONDS);
            startUnloadTimer(uuid);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return homes;
    }

    @Subcommand("use")
    @CommandCompletion("@players")
    @CommandPermission("essentials.command.homes.use")
    @Description("Allows a player to use another player's home")
    public void use(Player player, String[] args) {
        if (args.length >= 2) {
            String homeName = args[1];
            UUID uuid = Bukkit.getPlayerUniqueId(args[0]);
            if (uuid != null) {
                Map<String, Home> homes;
                if (!HomeManager.hasHomesLoaded(uuid)) {
                    // Load homes in
                    homes = loadHomes(uuid);
                    if (homes == null || homes.isEmpty()) {
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Could not retrieve homes for &f" + args[0]);
                        return;
                    }
                }
                Home home = HomeManager.getHomes(uuid).get(homeName);
                if (home != null) {
                    if (!home.getServerName().equals(Main.getInstance().SERVER_NAME)) {
                        ServerChange serverChange = new ServerChange(player.getUniqueId(), ServerChange.Reason.HOME_TELEPORT, Main.getInstance().SERVER_NAME, home.getServerName());
                        serverChange.addRedisInfo(home.toString());
                        serverChange.cache();
                        RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.toString());
                        serverChange.send();
                    } else {
                        player.teleport(home.getLocation());
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Teleported to home &f" + home.getName());
                    }
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The player &f" + args[0] + " &cdoes not exist");
            }
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Not enough arguments! Make sure you supply a player and a home name");
        }
    }

    @Subcommand("set")
    @CommandCompletion("@players")
    @CommandPermission("essentials.command.homes.set")
    @Description("Allows a player to set a home for another player")
    public void set(Player player, String[] args) {
        if (args.length >= 2) {
            String homeName = args[1];
            UUID uuid = Bukkit.getPlayerUniqueId(args[0]);
            if (uuid != null) {
                Map<String, Home> homes;
                if (!HomeManager.hasHomesLoaded(uuid)) {
                    // Load homes in
                    homes = loadHomes(uuid);
                    if (homes == null || homes.isEmpty()) {
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Could not retrieve homes for &f" + args[0]);
                        return;
                    }
                }
                Home home = new Home(uuid, homeName, Main.getInstance().SERVER_NAME, player.getLocation());
                RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_HOME, home.toString());
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Set home &f" + homeName + " &ato your location");
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The player &f" + args[0] + " &cdoes not exist");
            }
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Not enough arguments! Make sure you supply a player and a home name");
        }
    }

    @Subcommand("del")
    @CommandCompletion("@players")
    @CommandPermission("essentials.command.homes.del")
    @Description("Allows a player to delete a home for another player")
    public void del(Player player, String[] args) {
        if (args.length >= 2) {
            String homeName = args[1];
            UUID uuid = Bukkit.getPlayerUniqueId(args[0]);
            if (uuid != null) {
                Map<String, Home> homes;
                if (!HomeManager.hasHomesLoaded(uuid)) {
                    // Load homes in
                    homes = loadHomes(uuid);
                    if (homes == null || homes.isEmpty()) {
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Could not retrieve homes for &f" + args[0]);
                        return;
                    }
                } else {
                    homes = HomeManager.getHomes(uuid);
                }
                Home home = homes.get(homeName);
                if (home != null) {
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                        DatabaseEditor.deleteHome(home);
                        RedisConnector.getInstance().getConnection().publish(RedisAnnouncer.Channel.DEL_HOME.getChannel(), home.toString());
                    });
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Home &f" + homeName + " &a removed");
                } else {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "&f" + args[0] + " &cdoes not have a home &f" + homeName);
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The player &f" + args[0] + " &cdoes not exist");
            }
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Not enough arguments! Make sure you supply a player and a home name");
        }
    }
}
