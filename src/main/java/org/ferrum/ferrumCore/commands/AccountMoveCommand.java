package org.ferrum.ferrumCore.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.TabCompleterUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class AccountMoveCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Использование: /move_acc <nickFrom> <nickTo>");
            return true;
        }

        OfflinePlayer playerFrom = Bukkit.getOfflinePlayer(args[0]);
        OfflinePlayer playerTo = Bukkit.getOfflinePlayer(args[1]);


        if (playerFrom.isOnline()) {
            ((Player) playerFrom).kick(Component.text("Вы были удалены!"));
        }

        if (playerTo.isOnline()) {
            Bukkit.getPlayer(args[1]).kick(Component.text("Вы больше не вы!"));
        }

        String FromUUID = playerFrom.getUniqueId().toString();
        String ToUUID = playerTo.getUniqueId().toString();

        File playerData = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "playerdata/" + FromUUID + ".dat");
        File playerNewData = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "playerdata/" + ToUUID + ".dat");
        File playerDataFromBackup = new File(FerrumCore.plugin.getDataFolder(), "backup/playerdata/" + FromUUID + ".dat");
        File playerDataToBackup = new File(FerrumCore.plugin.getDataFolder(), "backup/playerdata/" + ToUUID + ".dat");

        File playerStats = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "stats/" + FromUUID + ".json");
        File playerNewStats = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "stats/" + ToUUID + ".json");
        File playerStatsFromBackup = new File(FerrumCore.plugin.getDataFolder(), "backup/stats/" + FromUUID + ".json");
        File playerStatsToBackup = new File(FerrumCore.plugin.getDataFolder(), "backup/stats/" + ToUUID + ".json");

        File playerAdvancement = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "advancements/" + FromUUID + ".json");
        File playerNewAdvancement = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "advancements/" + ToUUID + ".json");
        File playerAdvancementFromBackup = new File(FerrumCore.plugin.getDataFolder(), "backup/advancements/" + FromUUID + ".json");
        File playerAdvancementToBackup = new File(FerrumCore.plugin.getDataFolder(), "backup/advancements/" + ToUUID + ".json");

        try {
            playerDataFromBackup.getParentFile().mkdir();
            playerStatsFromBackup.getParentFile().mkdir();
            playerAdvancementFromBackup.getParentFile().mkdir();

            Files.copy(playerData.toPath(), playerDataFromBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(playerStats.toPath(), playerStatsFromBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(playerAdvancement.toPath(), playerAdvancementFromBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);

            if (playerNewData.exists()) {
                Files.copy(playerNewData.toPath(), playerDataToBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(playerNewStats.toPath(), playerStatsToBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(playerNewAdvancement.toPath(), playerAdvancementToBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            Files.move(
                    playerData.toPath(),
                    playerNewData.toPath(),
                    StandardCopyOption.REPLACE_EXISTING // заменяет файл, если он уже существует
            );

            Files.move(
                    playerStats.toPath(),
                    playerNewStats.toPath(),
                    StandardCopyOption.REPLACE_EXISTING // заменяет файл, если он уже существует
            );

            Files.move(
                    playerAdvancement.toPath(),
                    playerNewAdvancement.toPath(),
                    StandardCopyOption.REPLACE_EXISTING // заменяет файл, если он уже существует
            );

            sender.sendMessage("Определенный успех");
        } catch (IOException e) {
            FerrumCore.error(e.getMessage());
            sender.sendMessage("Ошибка при перемещении: " + e.getMessage());
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length < 3) {
            return TabCompleterUtil.offlineTabCompleter(strings[strings.length - 1]);
        }
        return List.of();
    }
}