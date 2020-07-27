package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.bukkit.Main;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

@CommandAlias("invsee")
@CommandPermission("essentials.command.invsee")
public class InvseeCmd extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Opens a crafting table for the player")
    public void invsee(Player player, String[] args) {
        if (args.length > 0) {
            String playerName = args[0];
            Player target = Bukkit.getPlayer(playerName);
            if (target != null) {
                boolean currentlyVanished = VanishAPI.isInvisible(player);
                // Ignore warnings, something's funky about what it's finding in the API. It does work!
                if (!currentlyVanished) {
                    VanishAPI.hidePlayer(player);
                }
                Main.getInstance().getServer().getPluginManager().callEvent(new PlayerInteractEntityEvent(player, target));
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.SUCCESS, "Opened &f" + playerName + "'s &ainventory.");
                if (!currentlyVanished) {
                    VanishAPI.showPlayer(player);
                }
            } else {
                WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The player &f" + playerName + "&cdoesn't exist. Perhaps they are on another server?");
            }
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "No player provided!");
        }
    }
}
