package com.westosia.essentials.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Text {

    public static BaseComponent format(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        return new TextComponent(text);
    }
}
