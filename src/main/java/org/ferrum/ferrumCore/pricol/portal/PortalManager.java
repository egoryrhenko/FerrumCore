package org.ferrum.ferrumCore.pricol.portal;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.chat.util.ChatUtil;
import org.ferrum.ferrumCore.managers.WorldsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PortalManager implements CommandExecutor, TabCompleter {

    public static List<Portal> portals = new ArrayList<>();

    private static final Map<String, List<String>> ARG_HINTS = Map.of(
            "world", List.of(),
            "size", List.of("1.0", "1.6", "2.0", "5.0"),
            "step", List.of("32", "64", "128"),
            "lifetime", List.of("100","200","300","400"),
            "activationtime", List.of("20","40","60","80"),
            "closetime", List.of("80","160","240","320"),
            "type", List.of("default", "move"),
            "teleportPos", List.of("0,0,0"),
            "frameParticle", List.of("FLAME", "SOUL_FIRE_FLAME","WAX_OFF", "WAX_ON", "TRIAL_OMEN", "TRIAL_SPAWNER_DETECTION_OMINOUS", "TRIAL_SPAWNER_DETECTION"),
            "target", List.of()
    );

    private Portal getPortal(int id) {
        return portals.stream().filter(p -> p.id == id).findFirst().orElse(null);
    }

    private void closeAllPortals() {
        Iterator<Portal> iterator = portals.iterator();
        while (iterator.hasNext()) {
            Portal portal = iterator.next();
            portal.close();
            iterator.remove(); // безопасное удаление
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatUtil.formatText("&eИспользуй: /portal open [аргументы]"));
            return true;
        }

        if (args[0].equalsIgnoreCase("closeAll")) {
            closeAllPortals();
            sender.sendMessage(ChatUtil.formatText("&cВсе порталы закрыты!"));
            return true;
        }

        if (args[0].equalsIgnoreCase("close") && args.length > 1) {
            int id = Integer.parseInt(args[1]);
            Portal portal = getPortal(id);
            portals.remove(portal);
            portal.close();
            sender.sendMessage(ChatUtil.formatText("&cВсе портал закрыт!"));
            return true;
        }

        if (!args[0].equalsIgnoreCase("open")) {
            sender.sendMessage(ChatUtil.formatText("&cНеизвестная подкоманда!"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.formatText("&cТолько для игроков."));
            return true;
        }

        // ---- Парсинг аргументов ----
        Map<String, String> parsed = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            String[] split = args[i].split(":", 2);
            if (split.length == 2) {
                parsed.put(split[0], split[1]);
            } else {
                parsed.put(split[0], "");
            }
        }

        // ---- Извлекаем значения с дефолтами ----
        String worldName = parsed.getOrDefault("world", getDefaultWorld(player.getWorld()));
        float size = parseFloatOrDefault(parsed.get("size"), 1.6f);
        int step = parseIntOrDefault(parsed.get("step"), 64);
        int lifetime = parseIntOrDefault(parsed.get("lifetime"), 200);
        int activationtime = parseIntOrDefault(parsed.get("activationtime"), 40);
        if (activationtime == 0) {
            activationtime = 1;
        }
        int closetime = parseIntOrDefault(parsed.get("closetime"), lifetime - activationtime);

        String type = parsed.getOrDefault("type", "default");
        String teleportPosRaw = parsed.getOrDefault("teleportPos", null);

        Particle frameParticle = null;
        if (parsed.containsKey("frameParticle")) {
            frameParticle = Particle.valueOf(parsed.get("frameParticle"));
            FerrumCore.log(frameParticle+"");
        }

        if (frameParticle == null) frameParticle = Particle.FLAME;

        World targetWorld = WorldsManager.getWorld(worldName, "NORMAL", "FLAT");

        // ---- Создание порталов ----
        Location fromLocation = getShiftedLocation(player, 5f).add(new Vector(0,1.8f,0));
        Location toLocation;

        if (teleportPosRaw == null) {
            toLocation = new Location(targetWorld, fromLocation.getX(), fromLocation.getY(), fromLocation.getZ());
        } else {
            String[] pos = teleportPosRaw.split(",");

            if (pos.length < 3) {
                sender.sendMessage("teleportPos error length");
                return true;
            }

            try {
                toLocation = new Location(targetWorld, Float.parseFloat(pos[0]), Float.parseFloat(pos[1]), Float.parseFloat(pos[2]));
            } catch (Exception ex) {
                sender.sendMessage(ex.getMessage());
                return true;
            }
        }
        while (!toLocation.getBlock().getType().isAir() && toLocation.getY() < 240) {
            toLocation.add(new Vector(0,1,0));
        }

        toLocation.add(new Vector(0f,1.5f,0f));
        if (toLocation.getY() > 239) {
            toLocation.setY(fromLocation.getY());
        }

        Vector rotation = new Vector(player.getPitch(), -player.getYaw(), 0f);

        Portal portal = new Portal(fromLocation, rotation, toLocation, size, lifetime, activationtime, closetime, step);
        portal.frameParticle = frameParticle;
        sender.sendMessage("Входной портал создан введет в " + toLocation.getWorld().getName() + ", id " + portal.id);
        portals.add(portal);
        Portal back = new Portal(toLocation, rotation, fromLocation, size,lifetime,activationtime, closetime, step);
        back.frameParticle = frameParticle;
        sender.sendMessage("Выходной портал создан введет в " + fromLocation.getWorld().getName() + ", id " + back.id);
        portals.add(back);
        return true;
    }

    private float parseFloatOrDefault(String s, float def) {
        if (s == null || s.isEmpty()) return def;
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return def;
        }
    }

    private int parseIntOrDefault(String s, int def) {
        if (s == null || s.isEmpty()) return def;
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    // ---- TAB ----
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("closeAll", "close", "open");
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("open")) {
            String last = args[args.length - 1];
            boolean hasColon = last.contains(":");

            // Собираем уже использованные ключи
            Set<String> used = new HashSet<>();
            for (int i = 1; i < args.length - 1; i++) {
                String[] split = args[i].split(":", 2);
                used.add(split[0].toLowerCase(Locale.ROOT));
            }

            if (!hasColon) {
                // Предлагаем ключи, которых ещё нет
                List<String> keys = new ArrayList<>(ARG_HINTS.keySet());
                keys.removeAll(used);
                return keys.stream()
                        .map(k -> k + ":")
                        .toList();
            } else {
                // Есть ключ — подставляем сразу key:value
                String[] split = last.split(":", 2);
                String key = split[0];
                String prefix = key + ":";


                List<String> hints = switch (key) {
                    case "world" -> Bukkit.getWorlds().stream().map(World::getName).toList();
                    case "target" -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                    default -> ARG_HINTS.getOrDefault(key, List.of());
                };

                // Возвращаем полный вариант: key:value
                return hints.stream()
                        .map(value -> prefix + value)
                        .toList();
            }
        }

        return List.of();
    }

    public Location getShiftedLocation(Player player, double distance) {
        // Получаем текущую позицию игрока
        Location playerLocation = player.getLocation();
        // Получаем направление по Yaw (без Pitch)
        Vector direction = getLookVector(player).normalize().multiply(distance);
        // Смещаем позицию вперёд
        return playerLocation.add(direction);
    }

    public Vector getLookVector(Player player) {
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        double yawRad = Math.toRadians(yaw); double pitchRad = Math.toRadians(pitch);
        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);
        return new Vector(x, y, z);
    }

    private String getDefaultWorld(World world) {
        return world.getName().equals("null") ? "world" : "null";
    }
}
