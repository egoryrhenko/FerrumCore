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

import java.awt.print.Paper;
import java.util.List;

public class WorldsManager implements CommandExecutor, TabCompleter {

    public static World createNewWorld(String worldName, World.Environment environment, WorldType worldType) {

        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            FerrumCore.log("[WorldManager] нашол старый мир");
            return world;
        }
        FerrumCore.log("[WorldManager] не нашол старый мир");
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
        if (args.length == 1) {
            if (commandSender instanceof Player player){
                player.teleport(createNewWorld(args[0], World.Environment.NORMAL, WorldType.FLAT).getSpawnLocation());
            }
        }
        if (args.length == 3){
            World world = createNewWorld(args[0], World.Environment.valueOf(args[1]), WorldType.getByName(args[2]));
            commandSender.sendMessage("Мир создан '" + args[0] + "' " + World.Environment.valueOf(args[1]).name() + " " + WorldType.getByName(args[2]));
            if (commandSender instanceof Player player){
                player.teleport(world.getSpawnLocation());
            }
        } else {
            commandSender.sendMessage("Args error");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> List.of("name");
            case 2 -> List.of("NORMAL", "NETHER", "THE_END");
            case 3 -> List.of("NORMAL", "AMPLIFIED", "FLAT", "LARGE_BIOMES");
            default -> List.of();
        };
    }
}