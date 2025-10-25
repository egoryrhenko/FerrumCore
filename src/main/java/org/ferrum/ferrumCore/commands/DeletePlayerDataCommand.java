package org.ferrum.ferrumCore.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.FerrumCore;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DeletePlayerDataCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length != 1) {
            sender.sendMessage("Использование: /move_acc <nickFrom> <nickTo>");
            return true;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

        if (player.isOnline()) {
            ((Player) player).kick(Component.text("Вы были удалены!"));
        }

        File playerData = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "playerdata/" + player.getUniqueId() + ".dat");
        File playerDataBackup = new File(FerrumCore.plugin.getDataFolder(), "backup/delete/playerdata/" + player.getUniqueId() + ".dat");

        File playerStats = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "stats/" + player.getUniqueId() + ".json");
        File playerStatsBackup = new File(FerrumCore.plugin.getDataFolder(), "backup/delete/stats/" + player.getUniqueId() + ".json");

        File playerAdvancement = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "advancements/" + player.getUniqueId() + ".json");
        File playerAdvancementBackup = new File(FerrumCore.plugin.getDataFolder(), "backup/delete/advancements/" + player.getUniqueId() + ".json");

        try {

            playerDataBackup.getParentFile().mkdir();
            playerStatsBackup.getParentFile().mkdir();
            playerAdvancementBackup.getParentFile().mkdir();

            Files.copy(playerData.toPath(), playerDataBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(playerStats.toPath(), playerStatsBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(playerAdvancement.toPath(), playerAdvancementBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);

            Files.delete(playerData.toPath());
            Files.delete(playerStats.toPath());
            Files.delete(playerAdvancement.toPath());

            sender.sendMessage("Определенный успех");

        } catch (IOException e) {
            FerrumCore.error(e.getMessage());
            sender.sendMessage("Ошибка при удалении: " + e.getMessage());
        }
        return true;
    }
}
