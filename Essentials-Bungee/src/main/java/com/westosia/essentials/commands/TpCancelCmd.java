package com.westosia.essentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.westosia.essentials.utils.TeleportRequest;
import com.westosia.essentials.utils.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Set;

@CommandAlias("tpcancel")
@CommandPermission("essentials.command.tpcancel")
public class TpCancelCmd extends BaseCommand {

    @Default
    @Description("Cancel a teleport request")
    public void tpcancel(ProxiedPlayer player, String[] args) {
        Set<ProxiedPlayer> cancelled = TeleportRequest.cancelRequest(player);
        if (cancelled.size() > 0) {
            player.sendMessage(Text.format("&2&l(!) &aCancelled your teleport request"));
        } else {
            player.sendMessage(Text.format("&4&l(!) &cNo request to cancel"));
        }
    }
}
