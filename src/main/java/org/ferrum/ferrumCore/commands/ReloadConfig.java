package org.ferrum.ferrumCore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ferrum.ferrumCore.managers.ConfigManager;
import org.jetbrains.annotations.NotNull;

public class ReloadConfig implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (ConfigManager.loadConfig()) {
            commandSender.sendMessage("Успех");
        } else {
            commandSender.sendMessage("Провал");
        }
        return true;
    }
}
