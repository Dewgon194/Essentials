package com.westosia.essentials.homes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.westosia.essentials.bukkit.Main;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.essentials.utils.ServerChange;
import com.westosia.westosiaapi.WestosiaAPI;
import com.westosia.westosiaapi.api.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@CommandAlias("home")
@CommandPermission("essentials.command.home")
public class HomeCmd extends BaseCommand {

    @Default
    @Description("Allows a player to teleport to their set homes")
    public void home(Player player, String[] args) {
        String homeName = "home";
        if (args.length > 0) {
            homeName = args[0];
        }
        Home home = HomeManager.getHome(player, homeName);
        if (home != null) {
            if (!home.getServerName().equalsIgnoreCase(Main.getInstance().serverName)) {
                //sendToServer(player, home.getServerName());
                ServerChange serverChange = new ServerChange(player.getUniqueId(), ServerChange.Reason.HOME_TELEPORT, Main.getInstance().serverName, home.getServerName());
                serverChange.send();
            }
            // Wait 2 ticks in case player was sent to another server
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> HomeManager.sendHomeData(home, player), 2);
        } else {
            WestosiaAPI.getNotifier().sendChatMessage(player, Notifier.NotifyStatus.ERROR, "The home &f" + homeName + " &cdoes not exist");
        }
    }
//TODO: not have these as static methods in a cmd class for cross class use
    /*
    public static void sendToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static void sendHomeData(Home home, Player playerUsing) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ForwardToPlayer");
        out.writeUTF(playerUsing.getName());
        out.writeUTF("EssentialsSendToHome");

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(home.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        playerUsing.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }*/
}
