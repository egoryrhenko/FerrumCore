package org.ferrum.ferrumCore.chat.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ferrum.ferrumCore.chat.util.ChatUtil;
import org.ferrum.ferrumCore.managers.save.SuffixData;
import org.ferrum.ferrumCore.utils.FerrumListener;

public class MinecraftMessagesListener extends FerrumListener {


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.joinMessage() == null) {
            return;
        }

        event.joinMessage(
                ChatUtil.formatText("&a→ ")
                        .append(ChatUtil.decorateNick(event.getPlayer().getName()))
                        .append(ChatUtil.formatText((" " + SuffixData.get(event.getPlayer()) + " &7зᴀшᴇл ʜᴀ ᴄᴇᴘʙᴇᴘ").replaceAll(" {2}", " ")))
        );

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerQuitEvent event) {
        if (event.quitMessage() == null) {
            return;
        }

        if (event.quitMessage().equals(ChatUtil.formatText("Подозрение на бота"))) {
            return;
        }

        event.quitMessage(ChatUtil.formatText(("&c←&f "+event.getPlayer().getName()+ " " + SuffixData.get(event.getPlayer()) + " &7ʙышᴇл ᴄ ᴄᴇᴘʙᴇᴘᴀ").replaceAll(" {2}", " ")));

    }


}