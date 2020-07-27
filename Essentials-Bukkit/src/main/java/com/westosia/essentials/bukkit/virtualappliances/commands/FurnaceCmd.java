package com.westosia.essentials.bukkit.virtualappliances.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.bukkit.virtualappliances.PlayerAppliances;
import com.westosia.essentials.bukkit.virtualappliances.VirtualFurnace;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import com.westosia.westosiaapi.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("furnace|smelt")
@CommandPermission("essentials.command.furnace")
public class FurnaceCmd extends BaseCommand {

    @Default
    @Description("Opens a furnace for the player")
    @CommandCompletion("set")
    public void furnace(Player player) {
        VirtualFurnace vurnace = PlayerAppliances.getAppliances(player.getUniqueId()).getFurnace();
        //vurnace.getFurnace().getChunk().load(true);
        vurnace.setForceLoadChunk(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> vurnace.open(player), 2);
        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Opened your portable furnace");
    }

    @Description("Sets the base furnace for the command to use")
    @Subcommand("set")
    public void set(Player player) {
        List<Block> blocks = player.getLineOfSight(null, 16);
        if (!blocks.isEmpty() && blocks.get(blocks.size() - 1).getType() == Material.FURNACE) {
            VirtualFurnace vurnace = new VirtualFurnace(player.getUniqueId(), (Furnace) blocks.get(blocks.size() - 1).getState());
            PlayerAppliances pa = PlayerAppliances.getAppliances(player.getUniqueId());
            pa.setFurnace(vurnace);
            //vurnace.setForceLoadChunk(true);
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Set the virtual furnace!");
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The block you are looking at is not a furnace.");
        }
    }
}
