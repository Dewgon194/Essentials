package com.westosia.essentials.utils;

import com.westosia.essentials.Main;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Text {

    public static BaseComponent format(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        return new TextComponent(text);
    }

    public static String getPrefix(ProxiedPlayer player) {
        LuckPerms lp = Main.LUCK_PERMS;
        User user = lp.getUserManager().getUser(player.getUniqueId());
        ContextManager cm = lp.getContextManager();
        CachedMetaData metaData = user.getCachedData().getMetaData(cm.getQueryOptions(user).orElse(cm.getStaticQueryOptions()));
        String prefix = metaData.getPrefix();
        if (prefix == null) {
            prefix = "&f";
        }
        return prefix;
    }
}
