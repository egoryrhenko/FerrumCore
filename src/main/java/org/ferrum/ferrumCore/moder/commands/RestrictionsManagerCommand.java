package org.ferrum.ferrumCore.moder.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.ferrum.ferrumCore.utils.TabCompleterUtil;
import org.ferrum.ferrumCore.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RestrictionsManagerCommand implements CommandExecutor, TabCompleter {

    private final Map<String, String> keys = new HashMap<>();
    private final UserManager manager;

    public RestrictionsManagerCommand() {
        keys.put("limit", "ferrum.restriction");
        keys.put("limit_knock", "ferrum.block.knock");
        keys.put("limit_mace", "ferrum.block.mace");
        keys.put("limit_shift", "ferrum.block.shift");
        keys.put("allow-fight", " ferrum.promotion.fight");
        keys.put("allow-lava", " ferrum.promotion.lava");
        keys.put("allow-place-danger", " ferrum.promotion.place");
        keys.put("allow-summon-danger", " ferrum.promotion.summon");

        manager = LuckPermsProvider.get().getUserManager();
    }

    public void withUser(
            String playerName,
            CommandSender sender,
            Consumer<User> action
    ) {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
        if (!offline.hasPlayedBefore()) {
            sender.sendMessage(Component.text("Игрок " + playerName + " не найден.", NamedTextColor.RED));
            return;
        }
        UUID uuid = offline.getUniqueId();
        CompletableFuture<User> future = manager.isLoaded(uuid)
                        ? CompletableFuture.completedFuture(manager.getUser(uuid))
                        : manager.loadUser(uuid);
        future.thenAccept(user -> {
            if (user == null) {
                sender.sendMessage(Component.text("Не удалось загрузить данные игрока " + playerName, NamedTextColor.RED));
                return;
            }

            action.accept(user);
            manager.saveUser(user);
        });
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

        withUser(playerName, sender, user -> {

            String permission = keys.get(key);

            switch (action) {
                case "add" -> handleAdd(sender, user, permission, args, label, playerName);
                case "remove" -> handleRemove(sender, user, permission, playerName);
                case "check" -> handleCheck(sender, user, permission, playerName);
                default -> sender.sendMessage("§cНеизвестное действие: " + action);
            }

        });

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

    private void handleAdd(CommandSender sender, User user, String permission,
                           String[] args, String label, String playerName) {

        if (args.length < 4) {
            sender.sendMessage("§cИспользование: /" + label + " <игрок> add <ключ> <время>");
            return;
        }

        Duration expiry = TimeUtils.parseTime(args[3]);

        if (expiry.isNegative()) {
            sender.sendMessage("§cВремя не может быть отрицательным");
            return;
        }

        if (expiry.toDays() > 30) {
            sender.sendMessage("§cМаксимальный срок — 30d");
            return;
        }

        Node node = PermissionNode.builder(permission)
                .value(true)
                .expiry(expiry)
                .build();

        user.data().add(node);

        sender.sendMessage("§aВыдано " + permission + " игроку " + playerName +
                " на " + TimeUtils.formatTime(expiry));
    }

    private void handleRemove(CommandSender sender, User user, String permission, String playerName) {
        Optional<PermissionNode> node = user.getNodes(NodeType.PERMISSION).stream()
                .filter(p -> p.getKey().equals(permission))
                .findFirst();

        if (node.isEmpty()) {
            sender.sendMessage("§cУ игрока нет " + permission);
            return;
        }

        user.data().remove(node.get());
        sender.sendMessage("§aУдалено " + permission + " у " + playerName);
    }

    private void handleCheck(CommandSender sender, User user, String permission, String playerName) {
        boolean hasPerm = user.getCachedData().getPermissionData()
                .checkPermission(permission).asBoolean();

        if (!hasPerm) {
            sender.sendMessage("§eУ игрока " + playerName + " нет " + permission);
            return;
        }

        Optional<PermissionNode> found = user.getNodes(NodeType.PERMISSION).stream()
                .filter(n -> n.getKey().equals(permission))
                .findFirst();

        if (found.isPresent() && found.get().hasExpiry()) {
            sender.sendMessage("§aУ " + playerName + " есть " + permission +
                    " ещё " + TimeUtils.formatTime(found.get().getExpiryDuration()));
        } else {
            sender.sendMessage("§aУ " + playerName + " есть " + permission + " без срока");
        }
    }
}
