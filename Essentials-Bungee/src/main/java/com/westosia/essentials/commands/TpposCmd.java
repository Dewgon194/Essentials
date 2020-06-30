package com.westosia.essentials.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.westosia.essentials.utils.Text;
import com.westosia.essentials.utils.teleports.Location;
import com.westosia.essentials.utils.teleports.Teleport;
import com.westosia.essentials.utils.teleports.TeleportTarget;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("tppos")
@CommandPermission("essentials.command.tppos")
public class TpposCmd extends BaseCommand {

    @Default
    @CommandCompletion("@servers @range:20")
    @Description("Teleport to specific coordinates")
    public void tppos(ProxiedPlayer player, String[] args) {
        if (args.length > 2) {
            int offset = 0;
            String server = player.getServer().getInfo().getName();
            // First arg is server
            if (!isNumber(args[0])) {
                server = args[0];
                offset++;
                if (ProxyServer.getInstance().getServerInfo(server) == null) {
                    player.sendMessage(Text.format("&4&l(!) &cInvalid world &f" + server + " &cgiven"));
                    return;
                }
            }
            double[] coords = new double[3];
            // Check other args to make sure they are numbers
            for (int i = offset; i < args.length; i++) {
                if (isNumber(args[i])) {
                    coords[i - offset] = Double.parseDouble(args[i]);
                } else {
                    player.sendMessage(Text.format("&4&l(!) &cArgument #" + i + " is not a valid coordinate"));
                    return;
                }
            }
            Location location = new Location(server, coords[0], coords[1], coords[2]);
            TeleportTarget<Location> target = new TeleportTarget<>();
            target.setType(location);
            new Teleport(player, target).use();
        } else {
            player.sendMessage(Text.format("&4&l(!) &cNot enough arguments! Make sure you have at least x, y, and z"));
        }
    }

    private boolean isNumber(String string) {
        return string.matches("-?\\d+(\\.\\d+)?");
    }
}
