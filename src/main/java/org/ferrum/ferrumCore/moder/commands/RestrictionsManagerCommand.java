package org.ferrum.ferrumCore.moder.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.ferrum.ferrumCore.utils.TabCompleterUtil;
import org.ferrum.ferrumCore.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

public class RestrictionsManagerCommand implements CommandExecutor, TabCompleter {

    private final Map<String, String> keys = new HashMap<>();
    private final LuckPerms luckPerms = LuckPermsProvider.get();
    private final UserManager manager;

    public RestrictionsManagerCommand() {
        keys.put("limit", "ferrum.restriction");
        keys.put("limit_knock", "ferrum.block.knock");
        keys.put("limit_mace", "ferrum.block.mace");
        keys.put("limit_shift", "ferrum.block.shift");

        manager = LuckPermsProvider.get().getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length < 3) {
            sender.sendMessage("§cИспользование: /" + label + " <игрок> <add|remove|check> <ключ> [время]");
            return true;
        }

        String playerName = args[0];
        String action = args[1].toLowerCase(Locale.ROOT);
        String key = args[2].toLowerCase(Locale.ROOT);

        if (!keys.containsKey(key)) {
            sender.sendMessage("§cНеизвестный ключ: " + key);
            return true;
        }


        User user = manager.getUser(playerName);

        if (user == null) {
            sender.sendMessage("§cИгрок " + playerName + " не найден.");
            return true;
        }

        String permission = keys.get(key);

        switch (action) {
            case "add" -> {
                if (args.length < 4) {
                    sender.sendMessage("§cИспользование: /" + label + " <игрок> add <ключ> <время>");
                    return true;
                }
                Duration expiry = TimeUtils.parseTime(args[3]);

                if (expiry.isNegative()) {
                    sender.sendMessage("Негатив время ноу ноу ноу");
                    return true;
                }

                if (expiry.toDays() > 30) {
                    sender.sendMessage("Максимальный допустимый строк 30d");
                    return true;
                }

                Node node = PermissionNode.builder(permission)
                            .value(true)
                            .expiry(expiry)
                            .build();

                user.data().add(node);
                manager.saveUser(user);

                sender.sendMessage("§aВыдано " + permission + " игроку " + playerName + " на "+ TimeUtils.formatTime(expiry));
            }

            case "remove" -> {
                Node node = user.getNodes(NodeType.PERMISSION).stream().filter(p -> p.getPermission().equals(permission)).findFirst().orElse(null);
                if (node == null) {
                    sender.sendMessage("Не найдено " + permission + " у " + playerName);
                    return true;
                }
                user.data().remove(node);
                luckPerms.getUserManager().saveUser(user);
                sender.sendMessage("§aУдалено разрешение " + permission + " у " + playerName);
            }

            case "check" -> {
                boolean hasPerm = user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
                if (!hasPerm) {
                    sender.sendMessage("§eУ игрока " + playerName + " нет " + permission);
                    return true;
                }

                // Проверяем, есть ли срок
                Optional<Node> found = user.getNodes().stream()
                        .filter(n -> n.getKey().equals(permission) && n instanceof PermissionNode)
                        .findFirst();

                if (found.isPresent() && found.get().hasExpiry()) {
                    Duration remaining = found.get().getExpiryDuration();
                    sender.sendMessage("§aУ " + playerName + " установлено " + permission +
                            " на " + TimeUtils.formatTime(remaining));
                } else {
                    sender.sendMessage("§aУ игрока " + playerName + " есть " + permission + " без срока.");
                }
            }

                default -> sender.sendMessage("§cНеизвестное действие: " + action);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                return TabCompleterUtil.offlineTabCompleter(args[0]); // Пусть Minecraft сам подсказывает имена игроков
            }
            case 2 -> {
                return List.of("add", "remove", "check");
            }
            case 3 -> {
                return new ArrayList<>(keys.keySet());
            }
            default -> {
                return List.of();
            }
        }
    }
}
