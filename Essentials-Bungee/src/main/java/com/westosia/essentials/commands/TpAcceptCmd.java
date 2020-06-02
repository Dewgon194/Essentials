package com.westosia.essentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.utils.TeleportRequest;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("tpaccept")
@CommandPermission("essentials.command.tpaccept")
public class TpAcceptCmd extends BaseCommand {

    @Default
    @Description("Accept a teleport request")
    public void tpaccept(ProxiedPlayer player, String[] args) {
        TeleportRequest request = TeleportRequest.getActiveTeleportRequest(player);

        if (request != null) {
            ProxiedPlayer requestSender = request.getSender();
            if (requestSender != null && requestSender.isConnected()) {
                request.use(true);
                player.sendMessage(new TextComponent("you have accepted the request from " + requestSender.getName()));
                requestSender.sendMessage(new TextComponent(player.getName() + " has accepted your request"));
            } else {
                player.sendMessage(new TextComponent("player no longer online"));
            }
        } else {
            player.sendMessage(new TextComponent("no active request found"));
        }
    }
}
