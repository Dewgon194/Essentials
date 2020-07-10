package com.westosia.essentials.homes.back;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.utils.RedisAnnouncer;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@CommandAlias("back")
@CommandPermission("essentials.command.back")
public class BackCmd extends BaseCommand {

    @Default
    @Description("Takes the player to a previous area")
    public void back(Player player, String[] args) {
        UUID uuid = player.getUniqueId();
        int backIndex = BackManager.getBackIndex(uuid);
        List<Home> backHomes = BackManager.getBackHomes(uuid);
        Home backHome = backHomes.get(backIndex);
        if (!backHome.getServerName().equalsIgnoreCase(Main.getInstance().SERVER_NAME)) {
            ServerChange serverChange = new ServerChange(player.getUniqueId(), ServerChange.Reason.BACK_TELEPORT, Main.getInstance().SERVER_NAME, backHome.getServerName());
            serverChange.addRedisInfo(backHome.toString());
            serverChange.cache();
            RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.CHANGE_SERVER, serverChange.toString());
            serverChange.send();
        } else {
            backHome.use();
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Teleported to previous location");
        }
        backIndex = calculateNewIndex(uuid);
        BackManager.setBackIndex(uuid, backIndex);
        RedisAnnouncer.tellRedis(RedisAnnouncer.Channel.SET_BACKHOME, uuid.toString() + ":" + backIndex);
    }

    private int calculateNewIndex(UUID uuid) {
        int backIndex = BackManager.getBackIndex(uuid);
        int size = BackManager.getBackHomes(uuid).size();

        if (backIndex >= size - 1) {
            backIndex = 0;
        } else {
            backIndex++;
        }
        return backIndex;
    }
}
