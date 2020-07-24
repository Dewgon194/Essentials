package com.westosia.essentials.bukkit.virtualappliances.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.essentials.bukkit.virtualappliances.PlayerAppliances;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.entity.Player;

@CommandAlias("furnace|smelt")
@CommandPermission("essentials.command.furnace")
public class FurnaceCmd extends BaseCommand {

    @Default
    @Description("Opens a furnace for the player")
    public void furnace(Player player) {
        PlayerAppliances.getAppliances(player.getUniqueId()).openFurnace();
        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Opened your portable furnace");
    }
}
