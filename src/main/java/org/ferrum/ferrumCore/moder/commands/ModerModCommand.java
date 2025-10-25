package org.ferrum.ferrumCore.moder.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.moder.ModerData;
import org.ferrum.ferrumCore.moder.ModerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModerModCommand implements TabCompleter, CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            commandSender.sendMessage(Component.text("Использование: " + command.getUsage(), NamedTextColor.RED));
            return true;
        }

        if (!(commandSender instanceof Player player)) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) commandSender.sendMessage("неизвестный "+args[0]);
            if (args.length > 1) commandSender.sendMessage("мало args ");
            switch (args[1]) {
                case "on" -> ModerManager.enableModerMod(player);
                case "off" -> ModerManager.disableModerMod(player);
            }
            return true;
        }

        boolean inModerMod = ModerManager.inModerMod(player);

        switch (args[0]) {
            case "on":

                if (inModerMod) {
                    player.sendMessage("Вы уже включили ModerMod");
                    return true;
                }

                if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                    player.sendMessage("Вы не можете включить ModerMod в режиме спектатора");
                    return true;
                }

                ModerManager.enableModerMod(player);
                player.sendMessage("Вы включили ModerMod");
                FerrumCore.alertRole(Component.text(player.getName() + " Включил ModerMod"), "ferrum.alert.modermod", player);

                return true;
            case "off":

                if (!inModerMod) {
                    player.sendMessage("Вы не включали ModerMod");
                    return true;
                }

                if (player.hasPermission("ferrum.modermod.bypass")) {
                    ModerManager.disableModerMod(player);
                    return true;
                }

                if (args.length < 3) {
                    player.sendMessage("Использование /moder off <Ник виновного игрока> <Причина>");
                    return true;
                }

                ModerManager.disableModerMod(player);

                player.sendMessage("Вы использовали ModMode для " + args[1] + ", по причине:" + String.join(" ", args));

                FerrumCore.alertRole(Component.text(player.getName() + " использовал ModMode для по причине: " + String.join(" ", args)), "ferrum.alert.modermod", player);
                return true;
            default:
                commandSender.sendMessage(Component.text("Использование: " + command.getUsage(), NamedTextColor.RED));
                return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> List.of(ModerManager.inModerMod((Player) commandSender) ? "off" : "on");
            case 2 -> {
                if (args[0].equals("off")) {
                    yield List.of("<nick>");
                }
                yield List.of();
            }
            default -> {
                if (args[0].equals("off")) {
                    yield List.of("<reason>");
                }
                yield List.of();
            }
        };
    }
}
