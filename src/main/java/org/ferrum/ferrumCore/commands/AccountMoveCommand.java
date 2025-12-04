package org.ferrum.ferrumCore.commands;

import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.hooks.LuckPermsHook;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.TabCompleterUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AccountMoveCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cИспользование: /move_acc <nickFrom> <nickTo>");
            return true;
        }

        OfflinePlayer from = Bukkit.getOfflinePlayer(args[0]);
        OfflinePlayer to = Bukkit.getOfflinePlayer(args[1]);

        if (!from.hasPlayedBefore()) {
            sender.sendMessage("§cИгрок §e" + args[0] + " §cникогда не был на сервере!");
            return true;
        }

        if (from.isOnline()) ((Player) from).kick(Component.text("§cВаши данные были перемещены."));
        if (to.isOnline()) ((Player) to).kick(Component.text("§cВаши данные были заменены."));

        String fromUUID = from.getUniqueId().toString();
        String toUUID = to.getUniqueId().toString();

        // Работает только с основным миром (обычно world)
        World world = Bukkit.getWorlds().getFirst();

        try {
            movePlayerFiles(sender, world, fromUUID, toUUID);
            moveLuckPerms(from.getUniqueId(), to.getUniqueId());
            sender.sendMessage("§aДанные успешно перемещены между §e" + args[0] + " §aи §e" + args[1]);
        } catch (Exception e) {
            FerrumCore.error("Ошибка при переносе аккаунта: " + e);
            sender.sendMessage("§cОшибка при переносе: " + e.getMessage());
        }
        return true;
    }

    private void movePlayerFiles(CommandSender sender, World world, String fromUUID, String toUUID) throws IOException {
        Path worldPath = world.getWorldFolder().toPath();

        moveAndBackup(worldPath.resolve("playerdata"), fromUUID + ".dat", toUUID + ".dat");
        moveAndBackup(worldPath.resolve("stats"), fromUUID + ".json", toUUID + ".json");
        moveAndBackup(worldPath.resolve("advancements"), fromUUID + ".json", toUUID + ".json");

        Files.delete(worldPath.resolve(worldPath.resolve("playerdata/" + fromUUID + ".dat_old")));

        sender.sendMessage("§7→ Данные перемещены в мире: §f" + world.getName());
    }

    private void moveAndBackup(Path dir, String oldName, String newName) throws IOException {
        Path src = dir.resolve(oldName);
        Path dst = dir.resolve(newName);

        if (!Files.exists(src)) return;

        Path backupDir = FerrumCore.plugin.getDataFolder().toPath().resolve("backup").resolve(dir.getFileName());
        Files.createDirectories(backupDir);

        // Копируем старый файл в backup
        Files.copy(src, backupDir.resolve(oldName), StandardCopyOption.REPLACE_EXISTING);

        // Копируем существующий целевой файл, если есть
        if (Files.exists(dst)) {
            Files.copy(dst, backupDir.resolve(newName), StandardCopyOption.REPLACE_EXISTING);
        }

        // Перемещаем старый файл в новый
        Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);
    }

    private void moveLuckPerms(UUID oldUUID, UUID newUUID) {
        if (!LuckPermsHook.isLuckPerms()) return;
        User oldUser = LuckPermsHook.luckPerms.getUserManager().getUser(oldUUID);
        if (oldUser == null) return;
        User newUser = LuckPermsHook.luckPerms.getUserManager().getUser(newUUID);
        if (newUser == null) return;

        oldUser.getNodes().forEach(node -> newUser.data().add(node));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length < 3) {
            return TabCompleterUtil.offlineTabCompleter(args[args.length - 1]);
        }
        return List.of();
    }
}
