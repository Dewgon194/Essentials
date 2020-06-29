package com.westosia.essentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.utils.teleports.TeleportRequest;
import com.westosia.essentials.utils.Text;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("tpa")
@CommandPermission("essentials.command.tpa")
public class TpaCmd extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Send a request to teleport to a player")
    public void tpa(ProxiedPlayer player, String[] args) {
        if (args.length > 0) {
            ProxiedPlayer playerSendTo = ProxyServer.getInstance().getPlayer(args[0]);
            if (playerSendTo != null) {
                if (!playerSendTo.equals(player)) {
                    TeleportRequest currentRequest = TeleportRequest.getActiveTeleportRequest(playerSendTo);
                    if (currentRequest != null && currentRequest.getSender().equals(player)) {
                        player.sendMessage(Text.format("&4&l(!) &cYou've already sent &f" + playerSendTo.getName() + "&c a request. Please wait a moment before sending them another"));
                    } else {
                        new TeleportRequest(player, playerSendTo, playerSendTo);
                        player.sendMessage(Text.format("&3&l(!) &bSent a request to teleport to &f" + playerSendTo.getName()));
                        playerSendTo.sendMessage(Text.format("&3&l(!) &f" + player.getName() + " &bhas requested to teleport to you"));
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
