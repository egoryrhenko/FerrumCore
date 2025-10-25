package org.ferrum.ferrumCore.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.chat.util.ChatUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlertRoleCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length > 1) {
            FerrumCore.alertRole(ChatUtil.formatText(ChatUtil.joinMessage(strings)), strings[0], null);
        } else {
            commandSender.sendMessage(Component.text("Использование: " + command.getUsage(), NamedTextColor.RED));
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 1) {
            return List.of("<permission>");
        }
        return List.of("<text>");
    }
}
