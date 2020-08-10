package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.utils.DatabaseEditor;
import com.westosia.essentials.utils.PowerToolManager;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CommandAlias("powertool|pt")
@CommandPermission("essentials.command.powertool")
public class PowerToolCmd extends BaseCommand {

    @Default
    @Description("Toggles whether power tools should be enabled")
    @CommandCompletion("set|remove")
    public void powertool(Player player) {
        boolean enabled = !PowerToolManager.hasPowerToolsEnabled(player);
        PowerToolManager.setHasPowerToolsEnabled(player, enabled);
        String stringEnabled = "&coff";
        if (enabled) {
            stringEnabled = "&aon";
        }
        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Your powertools have been toggled " + stringEnabled);
    }

    @Subcommand("set")
    @Description("Sets a command to the item in your hand. Don't include the slash!")
    public void set(Player player, String[] args) {
        Material material = player.getInventory().getItemInMainHand().getType();
        String cmd = StringUtils.join(args, " ");
        boolean updateTool = false;
        if (PowerToolManager.getPowerTools(player).containsKey(material)) {
            updateTool = true;
        }
        PowerToolManager.cachePowerTool(player.getUniqueId(), material, cmd);
        PowerToolManager.setHasPowerToolsEnabled(player, true);
        boolean finalUpdateTool = updateTool;
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> DatabaseEditor.savePowerTool(player.getUniqueId(), material, cmd, finalUpdateTool));
        WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Set item to run command &f" + cmd);
    }

    @Subcommand("remove")
    @Description("Removes the bound command from the item")
    public void remove(Player player, String[] args) {
        Material material = player.getInventory().getItemInMainHand().getType();
        if (PowerToolManager.getPowerTools(player).containsKey(material)) {
            PowerToolManager.uncachePowerTool(player.getUniqueId(), material);
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> DatabaseEditor.removePowerTool(player.getUniqueId(), material));
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Removed command from item!");
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "No command bound to this item");
        }
    }
}
