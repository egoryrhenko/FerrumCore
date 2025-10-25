package org.ferrum.ferrumCore.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.ferrum.ferrumCore.chat.util.ChatUtil;
import org.ferrum.ferrumCore.utils.TabCompleterUtil;
import org.ferrum.ferrumCore.managers.DonateManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LastSeenCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§cИспользование: /lastseen <ник>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target.isOnline()) {
            sender.sendMessage("§a" + target.getName() + " сейчас онлайн.");
            return true;
        }

        long lastSeen = target.getLastSeen();

        if (lastSeen == 0) {
            sender.sendMessage(ChatUtil.formatText("&eИгрок &f" + args[0] + "&e никогда не заходил на сервер."));
            return true;
        }

        String formatted = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(lastSeen));
        sender.sendMessage(ChatUtil.formatText( target.getName() + " был в сети: ")
                .append(
                        Component.text(formatted)
                                .hoverEvent(HoverEvent.showText(Component.text(DonateManager.getFormatTime((int) ((System.currentTimeMillis() - lastSeen) / 1000)) + " назад", NamedTextColor.GRAY)))
                )
        );
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
