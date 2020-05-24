package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

@CommandAlias("jump")
@CommandPermission("essentials.command.jump")
public class JumpCmd extends BaseCommand {

    @Default
    @Description("Sets item in players hand as a hat")
    public void jump(Player player) {

        Location loc = player.getLocation();
        List<Block> los = player.getLineOfSight(null, 32);
        Vector dir = player.getLocation().getDirection();
        List<Material> dangerSources = Arrays.asList(Material.LAVA, Material.MAGMA_BLOCK, Material.SWEET_BERRY_BUSH, Material.CACTUS, Material.FIRE, Material.CAMPFIRE, Material.WITHER_ROSE);

        int i = 0;
        for (i = 0; i < los.size(); i++) {
            if (dangerSources.contains(los.get(i).getType()) || dangerSources.contains(los.get(i).getLocation().add(0, 1, 0).getBlock().getType())) {
                player.sendMessage(ChatColor.RED + "You cant jump into a source of danger!");
                break;
            }else if (los.get(i).getType() != Material.AIR && los.get(i).getLocation().add(0, 1, 0).getBlock().getType() == Material.AIR) {
                player.teleport(los.get(i).getLocation().add(0.5, 1, 0.5).setDirection(dir));
                break;
            } else if (los.get(i).getType() != Material.AIR && los.get(i).getLocation().add(0, 1, 0).getBlock().getType() != Material.AIR) {
                player.teleport(los.get(i - 2).getLocation().add(0.5, -1, 0.5).setDirection(dir));
                break;
            } else if (los.get(i).getType() == Material.AIR && i == (los.size()-1)) {
                player.sendMessage(ChatColor.RED + "Target is out of range.");
                break;
            }
        }
    }
}
