package org.ferrum.ferrumCore.managers;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.event.player.PlayerClientOptionsChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.ferrum.ferrumCore.utils.FerrumListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.plaf.basic.BasicButtonUI;

public class RenderDistanceManager extends FerrumListener implements CommandExecutor {
    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.setViewDistance(getLimitedViewDistance(player,player.getClientOption(ClientOption.VIEW_DISTANCE)));
    }

    @EventHandler
    public void onClientOptionsChange(PlayerClientOptionsChangeEvent event) {
        Player player = event.getPlayer();
        player.setViewDistance(getLimitedViewDistance(player,event.getViewDistance()));

    }

    private int getLimitedViewDistance(Player player, int clientViewDistance) {
        // Проверка прав и установка лимита на дальность прорисовки
        if (player.hasPermission("ferrum.admin")) {
            return Math.min(clientViewDistance, 32);
        } else if (player.hasPermission("ferrum.donate_tier_2")) {
            return Math.min(clientViewDistance, 16);
        } else if (player.hasPermission("ferrum.donate_tier_1")) {
            return Math.min(clientViewDistance, 12);
        } else {
            return Math.min(clientViewDistance, 10);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length != 2) {
            commandSender.sendMessage("/command <nick> <value>");
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage("Кто это?");
            return true;
        }
        int value = Integer.parseInt(args[1]);
        player.setViewDistance(value);
        return true;
    }
}

