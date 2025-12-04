package org.ferrum.ferrumCore.managers;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.FerrumCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WorldsManager implements CommandExecutor, TabCompleter {

    public static World getWorld(String worldName) {
        return getWorld(worldName, World.Environment.NORMAL, WorldType.NORMAL);
    }

    public static World getWorld(String worldName, World.Environment environment, WorldType worldType) {

        World world = Bukkit.getWorlds()
                .stream()
                .filter(w -> w.getName().equals(worldName))
                .findFirst()
                .orElse(null);

        if (world != null) {
            FerrumCore.log("[WorldManager] нашёл старый мир");
            return world;
        }
        FerrumCore.log("[WorldManager] не нашёл старый мир");
        // Создаем WorldCreator с именем мира
        WorldCreator creator = new WorldCreator(worldName);

        // Настраиваем параметры мира
        creator.environment(environment); // Можно также выбрать NETHER или THE_END
        creator.type(worldType); // Можно также выбрать FLAT или LARGE_BIOMES

        // Создаем или загружаем мир
        world = Bukkit.createWorld(creator);

        world.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 0);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);

        return world;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        try {
            switch (args[0]) {
                case "create" -> {
                    if (args.length == 4) {
                        World world = getWorld(args[1], World.Environment.valueOf(args[2]), WorldType.getByName(args[3]));
                        commandSender.sendMessage("Создан мир: " + args[1] + ", Environment: " + args[2] + ", WorldType: " + args[3]);
                        if (commandSender instanceof Player player) {
                            player.teleport(world.getSpawnLocation());
                        }
                        return true;
                    }
                    commandSender.sendMessage("Args error");
                    return true;
                }

                case "tp" -> {
                    if (commandSender instanceof Player player) {
                        player.teleport(getWorld(args[1]).getSpawnLocation());
                        commandSender.sendMessage(player.getName() + " перемещен на точку спавна мира -> " + args[1]);
                        return true;
                    }
                    commandSender.sendMessage("простите извините но вы не наследуетесь от класса игрока");
                    return true;
                }

                case "unload" -> {
                    Bukkit.unloadWorld(args[1], true);
                    commandSender.sendMessage("отгружен мир -> " + args[1]);
                }

            }

            return true;
        } catch (Exception ex) {
            commandSender.sendMessage(ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> List.of("create","tp","unload");
            case 2 -> Bukkit.getWorlds().stream().map(World::getName).toList();
            case 3 -> args[0].equals("create") ? List.of("NORMAL", "NETHER", "THE_END") : List.of();
            case 4 -> args[0].equals("create") ? List.of("DEFAULT", "FLAT", "LARGEBIOMES", "AMPLIFIED") : List.of();
            default -> List.of();
        };
    }
}