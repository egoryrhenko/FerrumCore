package org.ferrum.ferrumCore.managers;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.managers.save.WorldsData;
import org.ferrum.ferrumCore.utils.FerrumCommand;
import org.ferrum.ferrumCore.utils.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldsManager extends FerrumCommand {

    public WorldsManager() {
        super("world");
        for (WorldsData.CustomWorld customWorld : WorldsData.getWorlds()) {
            getWorld(
                    customWorld.getWorldName(),
                    customWorld.getEnvironment(),
                    customWorld.getWorldType()
            );
        }
    }

    /* ===================== API ===================== */

    public static World getWorld(String name, String environment, String worldType) {
        World world = Bukkit.getWorlds().stream().filter(w -> w.getName().equals(name)).findFirst().orElse(null);

        if (world != null) {
            return world;
        }

        world = Bukkit.createWorld(
                new WorldCreator(name)
                        .environment(World.Environment.valueOf(environment))
                        .type(WorldType.valueOf(worldType))
        ); // ✅ safe (onEnable)
        FerrumCore.log("[WorldManager] создан мир -> " + name);
        return world;


    }
    public static World getWorld(String name) {
        return getWorld(name, "NORMAL", "FLAT");
    }

    /* ===================== COMMAND ===================== */

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String @NotNull [] args) {

        if (args.length == 0) {
            sender.sendMessage("§c/world <create|tp|unload|list>");
            return true;
        }

        switch (args[0].toLowerCase()) {

            /* ---------- CREATE ---------- */
            case "create" -> {

                if (Scheduler.isFolia()) {
                    sender.sendMessage("§cСоздание миров запрещено на Folia");
                    return true;
                }

                if (args.length != 4) {
                    sender.sendMessage("§c/world create <name> <env> <type>");
                    return true;
                }

                String name = args[1];
                World.Environment env = World.Environment.valueOf(args[2]);
                WorldType type = WorldType.getByName(args[3]);

                if (Bukkit.getWorld(name) != null) {
                    sender.sendMessage("§eМир уже загружен");
                    return true;
                }

                WorldCreator creator = new WorldCreator(name)
                        .environment(env)
                        .type(type);

                World world = Bukkit.createWorld(creator);

                sender.sendMessage("§aМир создан -> " + name);

                if (sender instanceof Player p) {
                    p.teleport(world.getSpawnLocation());
                }
            }

            /* ---------- TP ---------- */
            case "tp" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cТолько для игроков");
                    return true;
                }

                World world = getWorld(args[1]);
                if (world == null) {
                    sender.sendMessage("§cМир не загружен");
                    return true;
                }

                player.teleportAsync(world.getSpawnLocation());
                sender.sendMessage("§aТелепортирован -> " + world.getName());
            }

            /* ---------- UNLOAD ---------- */
            case "unload" -> {

                if (Scheduler.isFolia()) {
                    sender.sendMessage("§cВыгрузка миров запрещена на Folia");
                    return true;
                }

                String name = args[1];
                World world = Bukkit.getWorld(name);

                if (world == null) {
                    sender.sendMessage("§cМир не загружен");
                    return true;
                }

                Bukkit.unloadWorld(world, true);

                sender.sendMessage("§aМир выгружен -> " + name);
            }

            /* ---------- LIST ---------- */
            case "list" -> {
                sender.sendMessage("§6Загруженные миры:");
                Bukkit.getWorlds().forEach(w ->
                        sender.sendMessage(" §7- §a" + w.getName())
                );
            }
        }

        return true;
    }

    /* ===================== TAB ===================== */

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> List.of("create", "tp", "unload", "list");
            case 2 -> Bukkit.getWorlds().stream().map(World::getName).toList();
            case 3 -> args[0].equals("create")
                    ? List.of("NORMAL", "NETHER", "THE_END")
                    : List.of();
            case 4 -> args[0].equals("create")
                    ? List.of("NORMAL", "FLAT", "AMPLIFIED")
                    : List.of();
            default -> List.of();
        };
    }
}
