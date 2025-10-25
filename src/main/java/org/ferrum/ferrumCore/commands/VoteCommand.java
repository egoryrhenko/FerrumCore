package org.ferrum.ferrumCore.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.ferrum.ferrumCore.utils.TabCompleterUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class VoteCommand implements CommandExecutor, TabCompleter {

    private static final HashMap<String, HashSet<CommandSender>> voteList = new HashMap<>();
    private static final HashSet<String> whitelistNicks = new HashSet<>();
    private static boolean canVote;

    private static TextDisplay display;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        switch (args[0]) {
            case "player" -> {
                if (!canVote) {
                    commandSender.sendMessage(Component.text("Щас голосовать нельзя", NamedTextColor.RED));
                    return false;
                }
                String nick = args[1];

                if (!whitelistNicks.contains(nick)) {
                    commandSender.sendMessage("Неверный ник");
                    return true;
                }

                whitelistNicks.forEach(n -> voteList.computeIfAbsent(n, k -> new HashSet<>()).remove(commandSender));
                voteList.computeIfAbsent(nick, k -> new HashSet<>()).add(commandSender);

                commandSender.sendMessage("Вы проголосовали за " + nick);
                updateDisplay();

                return true;
            }

            case "get" -> {
                for (String nick : voteList.keySet()) {
                    commandSender.sendMessage("");
                    commandSender.sendMessage(nick + ": ");
                    for (CommandSender v : voteList.get(nick)) {
                        commandSender.sendMessage(" - "+v.getName());
                    }
                }
                return true;
            }
            case "add" -> {
                Component message = Component.text("Вы добавили: ");
                for (String nick : args[1].split(",")) {
                    whitelistNicks.add(nick.trim());
                    message = message.append(Component.text(nick + " "));
                }
                commandSender.sendMessage(message);
                updateDisplay();
                return true;
            }
            case "remove" -> {
                Component message = Component.text("Вы убрали: ");
                for (String nick : args[1].split(",")) {
                    whitelistNicks.remove(nick.trim());
                    message = message.append(Component.text(nick + " "));
                }
                commandSender.sendMessage(message);
                updateDisplay();
                return true;
            }
            case "clear" -> {
                commandSender.sendMessage("Вы убрали: " + String.join(" ", whitelistNicks));
                whitelistNicks.clear();
                voteList.clear();
                updateDisplay();
                return true;
            }

            case "start" -> {
                commandSender.sendMessage("Голосовать можно");
                canVote = true;
                return true;
            }

            case "stop" -> {
                commandSender.sendMessage("Голосовать не можно");
                canVote = false;
                return true;
            }

            case "spawn" -> {
                if (commandSender instanceof Player player) {
                    commandSender.sendMessage("Призвано");
                    display = player.getWorld().spawn(player.getLocation(), TextDisplay.class);
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (commandSender.hasPermission("ferrum.admin")) {
            return switch (args.length) {
                case 1 -> List.of("player", "add", "remove", "start", "stop", "spawn", "clear", "get");
                case 2 -> switch (args[0]) {
                    case "add" -> TabCompleterUtil.onlineTabCompleter(args[1].substring(args[1].lastIndexOf(",") + 1));
                    case "remove" -> whitelistNicks.stream().toList();
                    default -> List.of();
                };
                default -> List.of();
            };
        }
        return switch (args.length) {
            case 1 -> List.of("player");
            case 2 -> whitelistNicks.stream().toList();
            default -> List.of();
        };
    }

    public static void updateDisplay() {
        if (display == null) {
            return;
        }

        Component text = Component.empty();

        for (String nick : whitelistNicks) {
            if (!voteList.containsKey(nick)) {
                continue;
            }
            text = text
                    .append(Component.text(nick + ": "+voteList.get(nick).size()))
                    .append(Component.newline());
        }

        display.text(text);
    }
}
