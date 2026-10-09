package org.ferrum.ferrumCore.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.ferrum.ferrumCore.utils.TabCompleterUtil;
import org.ferrum.ferrumCore.suffixs.DonateManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LastSeenCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 1) {
            sender.sendRichMessage("<red> Используйте: /lastseen <ник>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target.isOnline()) {
            sender.sendRichMessage("<yellow>Игрок <white>" +target.getName() + " <yellow>сейчас онлайн");
            return true;
        }

        long lastSeen = target.getLastSeen();

        if (lastSeen == 0) {
            sender.sendRichMessage("<yellow>Игрок <white>" + args[0] + "<yellow> никогда не заходил на сервер");
            return true;
        }

        String formatted = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(lastSeen));
        String relative = DonateManager.getFormatTime((int) ((System.currentTimeMillis() - lastSeen) / 1000));
        sender.sendRichMessage("Игрок " + target.getName() + " был в сети <hover:show_text:'" + formatted + "'>" + relative + "</hover> назад");
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
