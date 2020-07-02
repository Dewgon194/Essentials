package com.westosia.essentials.homes.back;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.entity.Player;

@CommandAlias("back")
@CommandPermission("essentials.command.back")
public class BackCmd extends BaseCommand {

    @Default
    @Description("Takes the player to a previous area")
    public void back(Player player, String[] args) {
        //TODO: make this cross server and calculate the right backhome to go to
        BackManager.getBackHomes(player.getUniqueId()).get(0).use();
        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Teleported to previous location");
    }
}
