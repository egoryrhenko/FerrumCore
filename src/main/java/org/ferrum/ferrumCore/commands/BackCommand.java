package org.ferrum.ferrumCore.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.utils.FerrumCommand;
import org.ferrum.ferrumCore.utils.TeleportUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BackCommand extends FerrumCommand {

    private static final Map<UUID, Location> lastLocation = new HashMap<>();

    public static void saveLocation(Player player) {
        lastLocation.put(player.getUniqueId(), player.getLocation());
    }


    public BackCommand() {
        super("back");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Команда только для игроков");
            return true;
        }
        Location location = lastLocation.get(player.getUniqueId());

        if (location == null) {
            commandSender.sendMessage("Нету данных про ваше прошлое местоположение");
        }

        TeleportUtils.teleport(player, location);
        saveLocation(player);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String @NotNull [] args) {
        return List.of();
    }
}
