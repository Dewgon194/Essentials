package com.westosia.essentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.utils.teleports.TeleportRequest;
import com.westosia.essentials.utils.Text;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("tpahere")
@CommandPermission("essentials.command.tpahere")
public class TpaHereCmd extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Send a request to teleport a player to you")
    public void tpa(ProxiedPlayer player, String[] args) {
        if (args.length > 0) {
            ProxiedPlayer playerSendTo = ProxyServer.getInstance().getPlayer(args[0]);
            if (playerSendTo != null) {
                if (!playerSendTo.equals(player)) {
                    TeleportRequest currentRequest = TeleportRequest.getActiveTeleportRequest(playerSendTo);
                    if (currentRequest != null && currentRequest.getSender().equals(player)) {
                        player.sendMessage(Text.format("&4&l(!) &cYou've already sent &f" + playerSendTo.getName() + "&c a request. Please wait a moment before sending them another"));
                    } else {
                        new TeleportRequest(player, playerSendTo, player);
                        player.sendMessage(Text.format("&3&l(!) &bSent a request for &f" + playerSendTo.getName() + " &bto teleport to you"));
                        playerSendTo.sendMessage(Text.format("&3&l(!) &f" + player.getName() + " &bhas requested that you teleport to them"));
                    }
                } else {
                    player.sendMessage(Text.format("&4&l(!) &cYou cannot send yourself a teleport request"));
                }
            }
        } else {
            player.sendMessage(Text.format("&4&l(!) &cNo player specified!"));
        }
    }
}
