package com.westosia.essentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.essentials.utils.teleports.TeleportRequest;
import com.westosia.essentials.utils.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("tpdeny|tpignore")
@CommandPermission("essentials.command.tpdeny")
public class TpDenyCmd extends BaseCommand {

    @Default
    @Description("Deny a teleport request")
    public void tpdeny(ProxiedPlayer player, String[] args) {
        TeleportRequest request = TeleportRequest.getActiveTeleportRequest(player);
        if (request != null) {
            ProxiedPlayer requestSender = request.getSender();
            if (requestSender != null && requestSender.isConnected()) {
                request.use(false);
                player.sendMessage(Text.format("&2&l(!) &aYou've denied the request from &f" + requestSender.getName()));
            } else {
                player.sendMessage(Text.format("&4&l(!) &cThat player is no longer online"));
            }
        }
    }
}
