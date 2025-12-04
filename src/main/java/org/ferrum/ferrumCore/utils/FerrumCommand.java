package org.ferrum.ferrumCore.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class FerrumCommand extends Command {


    protected FerrumCommand(@NotNull String name) {
        super(name);
        setPermission("ferrum.command." + name);
        setDescription("auto generated FerrumCore command");
        setUsage("/" + name + " <???>");
    }
}