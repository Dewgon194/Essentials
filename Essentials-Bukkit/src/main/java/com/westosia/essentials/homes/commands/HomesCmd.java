package com.westosia.essentials.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.ServerChangeHelper;
import com.westosia.redisapi.redis.RedisConnector;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

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
                    Map<String, Home> homes = HomeManager.getHomes(uuid);
                    // Load homes in
                    if (!Bukkit.getOfflinePlayer(uuid).isOnline() && (homes == null || homes.isEmpty())) {
                        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                            // Get homes from database
                            Collection<Home> dbHomes = DatabaseEditor.getHomesInDB(uuid).values();
                            // Load them into Redis
                            dbHomes.forEach(home -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().SET_HOME_REDIS_CHANNEL, home.toString()));
                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                startUnloadTimer(uuid);
                                if (dbHomes.size() < 1) {
                                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "&f" + args[0] + " &chas no set homes");
                                } else {
                                    String homeList = getHomesList(homes.keySet());
                                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Found homes " + homeList);
                                }
                            });
                        });
                    } else {
                        String homeList = getHomesList(homes.keySet());
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Found homes " + homeList);
                    }
                } else {
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The player &f" + args[0] + "&cdoes not exist");
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
                ServerChangeHelper.saveHomesToDB(uuid.toString());
                Collection<Home> homes = new ArrayList<>(HomeManager.getHomes(uuid).values());
                // Ask other servers to uncache homes
                homes.forEach((home) -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().DEL_HOME_REDIS_CHANNEL, home.toString()));
            }, 2400).getTaskId();
            unload.put(uuid, id);
        }
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
                Map<String, Home> homes = HomeManager.getHomes(uuid);
                // Load homes in
                if (!Bukkit.getOfflinePlayer(uuid).isOnline() && (homes == null || homes.isEmpty())) {
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                        // Get homes from database
                        Map<String, Home> dbHomes = DatabaseEditor.getHomesInDB(uuid);
                        // Load them into Redis
                        dbHomes.values().forEach(home -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().SET_HOME_REDIS_CHANNEL, home.toString()));
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            startUnloadTimer(uuid);
                            if (dbHomes.size() < 1) {
                                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "&f" + args[0] + " &chas no set homes");
                            } else {
                                if (dbHomes.containsKey(homeName)) {
                                    Home home = dbHomes.get(homeName);
                                    if (!home.getServerName().equals(Main.getInstance().serverName)) {
                                        HomeCmd.sendToServer(player, home.getServerName());
                                    }
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> HomeCmd.sendHomeData(home, player), 2);
                                } else {
                                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "&f" + args[0] + " &cdoes not have a home &f" + homeName);
                                }
                            }
                        });
                    });
                } else {
                    Home home = HomeManager.getHomes(uuid).get(homeName);
                    if (home != null) {
                        if (!home.getServerName().equals(Main.getInstance().serverName)) {
                            HomeCmd.sendToServer(player, home.getServerName());
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> HomeCmd.sendHomeData(home, player), 2);
                    }
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The player &f" + args[0] + "&cdoes not exist");
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
                Map<String, Home> homes = HomeManager.getHomes(uuid);
                // Load homes in
                if (!Bukkit.getOfflinePlayer(uuid).isOnline() && (homes == null || homes.isEmpty())) {
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                        // Get homes from database
                        Map<String, Home> dbHomes = DatabaseEditor.getHomesInDB(uuid);
                        // Load them into Redis
                        dbHomes.values().forEach(home -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().SET_HOME_REDIS_CHANNEL, home.toString()));
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            startUnloadTimer(uuid);
                            Home home = new Home(uuid, homeName, Main.getInstance().serverName, player.getLocation());
                            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().SET_HOME_REDIS_CHANNEL, home.toString()));
                            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Set home &f" + homeName + " &ato your location");
                        });
                    });
                } else {
                    Home home = new Home(uuid, homeName, Main.getInstance().serverName, player.getLocation());
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().SET_HOME_REDIS_CHANNEL, home.toString()));
                    WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Set home &f" + homeName + " &ato your location");
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The player &f" + args[0] + "&cdoes not exist");
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
                Map<String, Home> homes = HomeManager.getHomes(uuid);
                // Load homes in if player is offline
                if (!Bukkit.getOfflinePlayer(uuid).isOnline() && (homes == null || homes.isEmpty())) {
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                        // Get homes from database
                        Map<String, Home> dbHomes = DatabaseEditor.getHomesInDB(uuid);
                        // Remove home
                        if (dbHomes.containsKey(homeName)) {
                            DatabaseEditor.deleteHome(dbHomes.get(homeName));
                            dbHomes.remove(homeName);
                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                startUnloadTimer(uuid);
                                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Home &f" + homeName + "&a removed");
                            });
                        } else {
                            // Did not remove a home because it didn't exist
                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "&f" + args[0] + " &cdoes not have a home &f" + homeName));
                        }
                        // Load them into Redis
                        dbHomes.values().forEach(home -> RedisConnector.getInstance().getConnection().publish(Main.getInstance().SET_HOME_REDIS_CHANNEL, home.toString()));
                    });
                } else {
                    Home home = homes.get(homeName);
                    if (home != null) {
                        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                            DatabaseEditor.deleteHome(home);
                            RedisConnector.getInstance().getConnection().publish(Main.getInstance().DEL_HOME_REDIS_CHANNEL, home.toString());
                        });
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Home &f" + homeName + "&a removed");
                    } else {
                        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "&f" + args[0] + " &cdoes not have a home &f" + homeName);
                    }
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The player &f" + args[0] + "&cdoes not exist");
            }
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "Not enough arguments! Make sure you supply a player and a home name");
        }
    }
}
