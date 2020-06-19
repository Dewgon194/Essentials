package com.westosia.essentials.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Map;

@CommandAlias("sethome")
@CommandPermission("essentials.command.sethome")
public class SetHomeCmd extends BaseCommand {

    @Default
    @Description("Sets a home to the player's location")
    public void setHome(Player player, String[] args) {
        String homeName = "home";
        if (args.length > 0) {
            homeName = args[0];
        }
        Map<String, Home> homes = HomeManager.getHomes(player);
        int size = 0;
        if (homes != null) {
            size = homes.size();
        }
        if (size < getHomesAmount(player)) {
            Home home = new Home(player, homeName, Main.getInstance().SERVER_NAME, player.getLocation());
            RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_HOME, home.toString());
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Set home &f" + homeName + " &ato your location");
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You have reached the max amount of homes you can set");
        }
    }

    private int getHomesAmount(Player player) {
        if (player.isOp() || player.hasPermission("*") || player.hasPermission("essentials.*")) return 100;

        String permissionExceptAmount = "essentials.homes.amount.";
        int highestAmount = 0;
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            if (perm.getValue() && perm.getPermission().startsWith("essentials.homes.amount.")) {
                String stringNum = perm.getPermission().substring(permissionExceptAmount.length());
                if (NumberUtils.isNumber(stringNum)) {
                    int amount = Integer.parseInt(stringNum);
                    if (amount > highestAmount) {
                        highestAmount = amount;
                    }
                }
            }
        }
        return highestAmount;
    }
}
