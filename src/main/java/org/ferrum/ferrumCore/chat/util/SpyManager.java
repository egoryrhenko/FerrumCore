package org.ferrum.ferrumCore.chat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ferrum.ferrumCore.managers.save.SpyEnableData;
import org.jetbrains.annotations.NotNull;

import java.util.EventListener;
import java.util.HashSet;

public class SpyManager implements CommandExecutor, Listener {
    public static HashSet<Player> spyEnable = new HashSet<>();


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (SpyEnableData.getValue().contains(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(Component.text("Включен режим spy").color(NamedTextColor.GRAY));
            spyEnable.add(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        spyEnable.remove(event.getPlayer());
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player player) {
            if (SpyEnableData.has(player.getName())) {
                spyEnable.remove(player);
                SpyEnableData.remove(player.getName());
                player.sendMessage("Spy отключен");
            } else {
                spyEnable.add(player);
                SpyEnableData.add(player.getName());
                player.sendMessage("Spy включен");
            }
        }
    return true;
    }
}
