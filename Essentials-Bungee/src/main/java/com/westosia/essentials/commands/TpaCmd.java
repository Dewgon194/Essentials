package com.westosia.essentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.utils.TeleportRequest;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
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
                TeleportRequest currentRequest = TeleportRequest.getActiveTeleportRequest(playerSendTo);
                if (currentRequest != null && currentRequest.getSender().equals(player)) {
                    player.sendMessage(new TextComponent("you've already sent this player a tp request. wait a moment"));
                } else {
                    new TeleportRequest(player, playerSendTo, playerSendTo);
                    player.sendMessage(new TextComponent("sent tpa request to " + playerSendTo.getName()));
                    playerSendTo.sendMessage(new TextComponent(player.getName() + " has requested to teleport to you"));
                }
            }
        }
    }
}
