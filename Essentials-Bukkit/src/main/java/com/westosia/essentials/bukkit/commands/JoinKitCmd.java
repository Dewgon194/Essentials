package com.westosia.essentials.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

@CommandAlias("joinkit")
@CommandPermission("essentials.command.joinkit")
public class JoinKitCmd extends BaseCommand {
    @Default
    @Description("Sets a kit to be given to the player on first join")
    public void joinKit(Player player) {
        ItemStack is = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta isMeta = (BookMeta) is.getItemMeta();
        isMeta.addPage("Hi there, and welcome to the &6Westosia &0closed &ealpha&0!" +
                "This is a very early version of our server, meaning you will most likely find &cbugs and glitches!" +
                "&0We will have a form you can fill out to help us after the...");
        isMeta.addPage("&ealpha&0, which will be a great area to give your feedback! :D" +
                "You can start the RPG/SMP adventure by doing &a/quests&0. A &afriend&0 will help you out! ;)" +
                "Enjoy!" +
                "-&6Westosia Team");
        is.setItemMeta(isMeta);
        player.getInventory().addItem(is);
    }

    private static Inventory joinKit = Bukkit.createInventory(null, 45, "&aJoinKit");

    public static void open(Player player) {
        ItemStack is = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta isMeta = is.getItemMeta();
        isMeta.setDisplayName(" ");
        is.setItemMeta(isMeta);
        ItemStack is2 = new ItemStack(Material.PAPER);
        ItemMeta is2Meta = is2.getItemMeta();
        is2Meta.setDisplayName(ChatColor.GREEN + "Save Kit");
        is2Meta.setCustomModelData(1);
        is2.setItemMeta(is2Meta);
        ItemStack is3 = new ItemStack(Material.BARRIER);
        ItemMeta is3Meta = is3.getItemMeta();
        is3Meta.setDisplayName("Cancel Kit");
        is3.setItemMeta(is3Meta);
        joinKit.setItem(36, is);
        joinKit.setItem(37, is);
        joinKit.setItem(38, is);
        joinKit.setItem(42, is);
        joinKit.setItem(43, is);
        joinKit.setItem(44, is);
        joinKit.setItem(40, is);
        joinKit.setItem(41, is2);
        joinKit.setItem(39, is3);

        player.openInventory(joinKit);
    }
}

