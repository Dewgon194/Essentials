package com.westosia.essentials.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
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
                if (player.getServer().getInfo() != target.getServer().getInfo()) {
                    player.connect(target.getServer().getInfo());
                }
                notifyBukkit(player, target.getName());
                announceTP(player.getName(), target.getName(), "");
            } else {
                player.sendMessage(
                        ChatMessageType.CHAT,
                        new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cCould not find player: " + args[0]))
                );
            }
        } else if (args.length == 2) {
            ProxiedPlayer source = ProxyServer.getInstance().getPlayer(args[0]);
            if (source != null) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                if (target != null) {
                    if (source.getServer().getInfo() != target.getServer().getInfo()) {
                        source.connect(target.getServer().getInfo());
                    }
                    notifyBukkit(source, target.getName());
                    announceTP(source.getName(), target.getName(), player.getName());
                } else {
                    player.sendMessage(
                            ChatMessageType.CHAT,
                            new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cCould not find player: " + args[1]))
                    );
                }
            } else {
                player.sendMessage(
                        ChatMessageType.CHAT,
                        new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cCould not find player: " + args[0]))
                );
            }
        } else {
            player.sendMessage(
                    ChatMessageType.CHAT,
                    new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cPlease provide a player to teleport to!"))
            );
        }
    }

    private void notifyBukkit(ProxiedPlayer source, String target) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("EssentialsTP");
        out.writeUTF(source.getName());
        out.writeUTF(target);

        source.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
    }

    private void announceTP(String source, String target, String force) {
        ProxyServer.getInstance().getPlayers().forEach(player -> {
            if (player.hasPermission("essentials.command.teleport")) {
                if (force.isEmpty()) {
                    player.sendMessage(
                            ChatMessageType.CHAT,
                            new TextComponent(ChatColor.translateAlternateColorCodes(
                                    '&',
                                    "&3&l(!) &3" + source + " &bhas teleported to &3" + target + "&b!")
                            )
                    );
                } else {
                    player.sendMessage(
                            ChatMessageType.CHAT,
                            new TextComponent(ChatColor.translateAlternateColorCodes(
                                    '&',
                                    "&3&l(!) &3" + force + " &bhas teleported &3" + source + " &bto &3" + target + "&b!")
                            )
                    );
                }
            }
        });
    }

}
