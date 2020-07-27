package com.westosia.essentials.bukkit.virtualappliances.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.bukkit.virtualappliances.ApplianceManager;
import com.westosia.essentials.bukkit.virtualappliances.VirtualBrewingStand;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("brew|brewingstand|bs|brewing")
@CommandPermission("essentials.command.brew")
public class BrewCmd extends BaseCommand {

    @Default
    @Description("Opens a brewing stand for the player")
    @CommandCompletion("set")
    public void brew(Player player) {
        VirtualBrewingStand virtualBrewingStand = ApplianceManager.getAppliances(player.getUniqueId()).getBrewingStand();
        virtualBrewingStand.setForceLoadChunk(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> virtualBrewingStand.open(player), 2);
        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Opened your portable furnace");
    }

    @Description("Sets the base brewing stand for the command to use")
    @Subcommand("set")
    public void set(Player player) {
        List<Block> blocks = player.getLineOfSight(null, 16);
        if (!blocks.isEmpty() && blocks.get(blocks.size() - 1).getType() == Material.BREWING_STAND) {
            VirtualBrewingStand virtualBrewingStand = new VirtualBrewingStand(player.getUniqueId(), (BrewingStand) blocks.get(blocks.size() - 1).getState());
            ApplianceManager pa = ApplianceManager.getAppliances(player.getUniqueId());
            pa.setBrewingStand(virtualBrewingStand);
            //vurnace.setForceLoadChunk(true);
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Set the virtual brewing stand!");
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The block you are looking at is not a brewing stand.");
        }
    }
}
