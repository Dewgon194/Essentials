package com.westosia.essentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.utils.TeleportRequest;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
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
                TeleportRequest currentRequest = TeleportRequest.getActiveTeleportRequest(playerSendTo);
                if (currentRequest != null && currentRequest.getSender().equals(player)) {
                    player.sendMessage(new TextComponent("you've already sent this player a tp request. wait a moment"));
                } else {
                    new TeleportRequest(player, playerSendTo, player);
                    player.sendMessage(new TextComponent("sent tpahere request to " + playerSendTo.getName()));
                    playerSendTo.sendMessage(new TextComponent(player.getName() + " has requested that you teleport to them"));
                }
            }
        }
    }
}
