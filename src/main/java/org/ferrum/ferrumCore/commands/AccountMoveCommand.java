package org.ferrum.ferrumCore.commands;

import net.kyori.adventure.text.Component;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.hooks.LuckPermsHook;
import org.ferrum.ferrumCore.utils.TabCompleterUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class AccountMoveCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 2) {
            sender.sendRichMessage("<red>Использование: /move_acc <nickFrom> <nickTo>");
            return true;
        }

        OfflinePlayer from = Bukkit.getOfflinePlayerIfCached(args[0]);
        if (from == null) {
            from = Bukkit.getOfflinePlayer(args[0]);
        }
        OfflinePlayer to = Bukkit.getOfflinePlayerIfCached(args[1]);
        if (to == null) {
            to = Bukkit.getOfflinePlayer(args[1]);
        }

        if (!from.hasPlayedBefore()) {
            sender.sendMessage(Component.text("§cИгрок §e" + args[0] + " §cникогда не был на сервере!"));
            return true;
        }

        if (from.isOnline() && from.getPlayer() != null) {
            from.getPlayer().kick(Component.text("§cВаш аккаунт перемещается"));
        }
        if (to.isOnline() && to.getPlayer() != null) {
            to.getPlayer().kick(Component.text("§cВаш аккаунт заменён"));
        }

        String fromUUID = from.getUniqueId().toString();
        String toUUID = to.getUniqueId().toString();

        try {
            movePlayerFiles(sender, Bukkit.getWorlds().getFirst(), fromUUID, toUUID, args[1], args[0]);

            moveLuckPerms(from.getUniqueId(), to.getUniqueId());

            sender.sendMessage(Component.text("§aДанные успешно перемещены: §e" + args[0] + " §a→ §e" + args[1]));
        } catch (Exception e) {
            e.printStackTrace();
            FerrumCore.error("Ошибка при переносе аккаунта: " + e.getMessage());
            sender.sendMessage(Component.text("§cОшибка при переносе: §e" + e.getMessage()));
        }
        return true;
    }

    private void movePlayerFiles(CommandSender sender, World world, String fromUUID, String toUUID, String newPlayerName, String oldPlayerName) throws IOException {
        // Paper 1.21.11: getWorldFolder() уже возвращает папку мира (world, world_nether и т.д.)
        Path worldPath = Path.of(world.getWorldFolder().getPath());

        moveAndBackup(worldPath.resolve("playerdata"), fromUUID + ".dat", toUUID + ".dat", newPlayerName, oldPlayerName);
        moveAndBackup(worldPath.resolve("stats"), fromUUID + ".json", toUUID + ".json", newPlayerName, oldPlayerName);
        moveAndBackup(worldPath.resolve("advancements"), fromUUID + ".json", toUUID + ".json", newPlayerName, oldPlayerName);

        Path oldDat = worldPath.resolve("playerdata").resolve(fromUUID + ".dat_old");
        if (Files.exists(oldDat)) {
            Files.delete(oldDat);
        }

        sender.sendMessage(Component.text("§7→ Данные перемещены в мире: §f" + world.getName()));
    }

    private void moveAndBackup(Path dir, String oldName, String newName, String newPlayerName, String oldPlayerName) throws IOException {
        Path src = dir.resolve(oldName);
        Path dst = dir.resolve(newName);

        if (!Files.exists(src)) {
            FerrumCore.plugin.getLogger().warning("Файл не найден: " + src);
            return;
        }

        Path backupDir = FerrumCore.plugin.getDataFolder().toPath()
                .resolve("backup").resolve(dir.getFileName());
        Files.createDirectories(backupDir.resolve(oldPlayerName));

        try {
            // 1. Бэкап исходного файла
            Files.copy(src, backupDir.resolve(oldPlayerName).resolve(oldName), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

            // 2. Бэкап целевого файла (если есть)
            if (Files.exists(dst)) {
                Files.createDirectories(backupDir.resolve(newPlayerName));
                Files.copy(dst, backupDir.resolve(newPlayerName).resolve(newName), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            }

            // 3. Перемещаем
            Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);

            FerrumCore.plugin.getLogger().info("✅ " + oldName + " → " + newName);
        } catch (Exception e) {
            e.printStackTrace();
            FerrumCore.error("❌ " + oldName + ": " + e.getMessage());
        }
    }

    private void moveLuckPerms(java.util.UUID oldUUID, java.util.UUID newUUID) {
        if (FerrumCore.noLuckperms || LuckPermsHook.luckPerms == null) return;

        User oldUser = LuckPermsHook.luckPerms.getUserManager().getUser(oldUUID);
        User newUser = LuckPermsHook.luckPerms.getUserManager().getUser(newUUID);

        if (oldUser == null || newUser == null) return;

        oldUser.getNodes().forEach(node -> {
            if (!node.getKey().startsWith("group.")) {
                newUser.data().add(node);
            }
        });

        LuckPermsHook.luckPerms.getUserManager().saveUser(newUser);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 || args.length == 2) {
            return TabCompleterUtil.offlineTabCompleter(args[args.length - 1]);
        }
        return List.of();
    }
}