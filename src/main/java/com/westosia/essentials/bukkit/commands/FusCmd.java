package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/*
    Author: Josh Duggan
    Description: This command allows players with the permission to propel entities away from them.
    /fus will propel entities away from the player gaining strength when using /fus ro, and /fus ro dah

 */

@CommandAlias("fus")
@CommandPermission("essentials.command.fus")
public class FusCmd extends BaseCommand {

    @Default
    @Description("Launches entities away from the player with variable strength.")
    public void fus(Player player, String[] args) {
        Location loc = player.getLocation();
        if (args.length == 0) {
            ArrayList<Entity> shoutVictims = new ArrayList<Entity>();
            shoutVictims.addAll(player.getNearbyEntities(4.5, 4.5, 4.5));
            for (Entity victim : shoutVictims) {
                if (victim instanceof LivingEntity && victim != player) {
                    victim.setVelocity(player.getLocation().getDirection().multiply(1));
                }
            }
            if (shoutVictims.size() > 0) {
                player.sendMessage(ChatColor.AQUA + "FUS!");
                WestosiaAPI.getSoundEmitter().playSound(loc, 10, Sound.ENTITY_RAVAGER_ROAR);
            } else if (shoutVictims.size() == 0) {
                player.sendMessage(ChatColor.RED + "Your Godly Shout reached no ears!");
            }
        } else if (args.length == 1 && args[0].equals("ro")) {
            ArrayList<Entity> shoutVictims = new ArrayList<Entity>();
            shoutVictims.addAll(player.getNearbyEntities(4.5, 4.5, 4.5));
            for (Entity victim : shoutVictims) {
                if (victim instanceof LivingEntity && victim != player) {
                    victim.setVelocity(player.getLocation().getDirection().multiply(2));
                }

            }
            if (shoutVictims.size() > 0) {
                player.sendMessage(ChatColor.AQUA + "FUS RO!");
                WestosiaAPI.getSoundEmitter().playSound(loc, 10, Sound.ENTITY_RAVAGER_ROAR);
            } else if (shoutVictims.size() == 0) {
                player.sendMessage(ChatColor.RED + "Your Godly Shout reached no ears!");
            }
        } else if (args.length == 2 && args[0].equals("ro") && args[1].equals("dah")) {
            ArrayList<Entity> shoutVictims = new ArrayList<Entity>();
            shoutVictims.addAll(player.getNearbyEntities(4.5, 4.5, 4.5));
            for (Entity victim : shoutVictims) {
                if (victim instanceof LivingEntity && victim != player) {
                    victim.setVelocity(player.getLocation().getDirection().multiply(3));
                }
            }
            if (shoutVictims.size() > 0) {
                player.sendMessage(ChatColor.AQUA + "FUS RO DAH!");
                WestosiaAPI.getSoundEmitter().playSound(loc, 10, Sound.ENTITY_RAVAGER_ROAR);
            } else if (shoutVictims.size() == 0) {
                player.sendMessage(ChatColor.RED + "Your Godly Shout reached no ears!");
            }
        } else if (args.length > 2){
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "You have entered too many arguments!");
        }
    }
}

