package org.ferrum.ferrumCore.commands;


import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.ferrum.ferrumCore.utils.TabCompleterUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GetHourCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Использование: /playtime <имя игрока>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);


        int time = target.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;

        if (time == 0) {
            sender.sendMessage("Игрок не найден");
            return true;
        }

        sender.sendMessage(target.getName() + " провел в игре: " + (time / 3600) + " часов и " + (time / 60) % 60 + " минут");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 1) {
            return TabCompleterUtil.offlineTabCompleter(strings[0]);
        }
        return List.of();
    }
}