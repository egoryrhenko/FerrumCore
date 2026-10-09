package org.ferrum.ferrumCore.utils;

import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.ferrum.ferrumCore.FerrumCore;
import org.jetbrains.annotations.NotNull;


public class FerrumCommand extends Command implements PluginIdentifiableCommand {

    public FerrumCommand(@NotNull String name) {
        super(name);
        setPermission("ferrum.command." + name);
        setDescription("auto generated FerrumCore command");
        setUsage("/" + name + " <???>");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {
        return false;
    }

    public FerrumCommand(@NotNull String name, Boolean hasPermission) {
        super(name);
        if (hasPermission) {
            setPermission("ferrum.command." + name);
        }
        setDescription("auto generated FerrumCore command");
        setUsage("/" + name + " <???>");
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return FerrumCore.plugin;
    }

}