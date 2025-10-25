package org.ferrum.ferrumCore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class RollbackCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();

        Bukkit.dispatchCommand(console, "co rollback u:" + args[0] + ",#tnt-" + args[0] + ",#fire-" + args[0] + ",#creeper-" + args[0] + " t:1d r:#global");
        return false;
    }
}
