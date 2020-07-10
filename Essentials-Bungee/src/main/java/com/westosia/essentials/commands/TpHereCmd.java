package com.westosia.essentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.utils.Text;
import com.westosia.essentials.utils.teleports.Teleport;
import com.westosia.essentials.utils.teleports.TeleportTarget;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("tphere|teleporthere")
@CommandPermission("essentials.command.teleporthere")
public class TpHereCmd extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Teleport a player to you")
    public void teleport(ProxiedPlayer player, String[] args) {
        if (args.length > 0) {
            ProxiedPlayer victim = ProxyServer.getInstance().getPlayer(args[0]);
            if (victim != null) {
                TeleportTarget<ProxiedPlayer> goingTo = new TeleportTarget<>();
                goingTo.setType(player);
                new Teleport(victim, goingTo).use();
                announceTP(player.getName(), victim.getName());
            } else {
                player.sendMessage(Text.format("&4&l(!) &cCould not find player: " + args[0]));
            }
        } else {
            player.sendMessage(Text.format("&4&l(!) &cNo player specified!"));
        }
    }

    private void announceTP(String source, String target) {
        ProxyServer.getInstance().getPlayers().forEach(player -> {
            if (player.hasPermission("essentials.command.teleporthere")) {
                player.sendMessage(Text.format("&3&l(!) &3" + source + " &bhas teleported &3" + target + " &bto them!"));
            }
        });
    }
}
