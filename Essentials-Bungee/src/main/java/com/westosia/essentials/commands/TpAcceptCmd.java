package com.westosia.essentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.utils.teleports.TeleportRequest;
import com.westosia.essentials.utils.Text;
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
                player.sendMessage(Text.format("&2&l(!) &aYou have accepted the request from &f" + requestSender.getName()));
                requestSender.sendMessage(Text.format("&2&l(!) &f" + player.getName() + "&a has accepted your request"));
            } else {
                player.sendMessage(Text.format("&4&l(!) &cThat player is no longer online"));
            }
        } else {
            player.sendMessage(Text.format("&4&l(!) &cNo active request right now"));
        }
    }
}
