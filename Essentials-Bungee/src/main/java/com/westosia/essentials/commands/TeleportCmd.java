package com.westosia.essentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.utils.TeleportRequest;
import com.westosia.essentials.utils.Text;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("tp|teleport")
@CommandPermission("essentials.command.teleport")
public class TeleportCmd extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Teleport to players")
    public void teleport(ProxiedPlayer player, String[] args) {
        if (args.length == 1) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target != null) {
                new TeleportRequest(player, target, target).use(true);
                announceTP(player.getName(), target.getName(), "");
            } else {
                player.sendMessage(Text.format("&cCould not find player: " + args[0]));
            }
        } else if (args.length == 2) {
            ProxiedPlayer source = ProxyServer.getInstance().getPlayer(args[0]);
            if (source != null) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                if (target != null) {
                    new TeleportRequest(source, target, target).use(true);
                    announceTP(source.getName(), target.getName(), player.getName());
                } else {
                    player.sendMessage(Text.format("&cCould not find player: " + args[1]));
                }
            } else {
                player.sendMessage(Text.format("&cCould not find player: " + args[0]));
            }
        } else {
            player.sendMessage(Text.format("&cPlease provide a player to teleport to!"));
        }
    }

    private void announceTP(String source, String target, String force) {
        ProxyServer.getInstance().getPlayers().forEach(player -> {
            if (player.hasPermission("essentials.command.teleport")) {
                if (force.isEmpty()) {
                    player.sendMessage(Text.format("&3&l(!) &3" + source + " &bhas teleported to &3" + target + "&b!"));
                } else {
                    player.sendMessage(Text.format("&3&l(!) &3" + force + " &bhas teleported &3" + source + " &bto &3" + target + "&b!"));
                }
            }
        });
    }

}
