package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.westosiaapi.WestosiaAPI;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/*
    Author: Jamie Cee
    Description: This command allows the user to cast a fireball in the direction that they are aiming.
 */

@CommandAlias("fireball|fire|fb")
@CommandPermission("essentials.command.fireball")
public class FireballCmd extends BaseCommand {

    @Default
    @Description("Sets a fireball in the direction facing")
    public void fireball(Player player) {
        Location loc = player.getLocation();
        Entity fireball = player.getWorld().spawnEntity(loc, EntityType.FIREBALL);
        fireball.setVelocity(loc.getDirection().multiply(2));
        WestosiaAPI.getSoundEmitter().playSound(player, Sound.ENTITY_ENDER_DRAGON_SHOOT);

    }
}
