package com.westosia.essentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.utils.Text;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("seen")
@CommandPermission("essentials.command.seen")
public class SeenCmd extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Get last seen information for a player")
    public void seen(ProxiedPlayer player, String[] args) {
        if (args.length > 0) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target != null) {
                queryInfo(args[0], player, false);
            } else {
                queryInfo(args[0], player, true);
            }
        } else {
            player.sendMessage(Text.format("&4&l(!) &cNo player specified!"));
        }
    }

    private void queryInfo(String target, ProxiedPlayer sender, boolean offline) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("querySeen");
        if (offline) {
            output.writeUTF("offline");
        } else {
            output.writeUTF("online");
        }
        output.writeUTF(sender.getName());
        output.writeUTF(target);

        if (offline) {
            sender.getServer().sendData("BungeeCord", output.toByteArray());
        } else {
            ProxyServer.getInstance().getPlayer(target).getServer().sendData("BungeeCord", output.toByteArray());
        }
    }
}
