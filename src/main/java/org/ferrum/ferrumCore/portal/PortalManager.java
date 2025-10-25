package org.ferrum.ferrumCore.portal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.ferrum.ferrumCore.chat.util.ChatUtil;
import org.ferrum.ferrumCore.managers.WorldsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PortalManager implements CommandExecutor, TabCompleter {

    List<Portal> portals = new ArrayList<>();

    private Portal getPortal(int id) {
        return portals.stream().filter(p -> p.id == id).findFirst().orElse(null);
    }

    private void closeAllPortals() {
        portals.forEach(
                portal -> portal.close()
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        switch (strings.length) {
            case 0: // none
                if (commandSender instanceof Player player) {
                    Location portalLocation = getShiftedLocation(player, 5f).add(new Vector(0,1.8f,0));
                    Location locationToTeleport = portalLocation.clone();

                    if (portalLocation.getWorld().getName().equals("null")) {
                        locationToTeleport.setWorld(WorldsManager.createNewWorld("world", World.Environment.NORMAL, WorldType.LARGE_BIOMES));
                    } else {
                        locationToTeleport.setWorld(WorldsManager.createNewWorld("null", World.Environment.NORMAL, WorldType.LARGE_BIOMES));
                    }


                    while (!locationToTeleport.getBlock().getType().isAir() && locationToTeleport.getY() < 240) {
                        locationToTeleport.add(new Vector(0,1,0));
                    }

                    locationToTeleport.add(new Vector(0f,1.5f,0f));

                    if (locationToTeleport.getY() > 239) {
                        locationToTeleport.setY(portalLocation.getY());
                    }

                    Vector rotation = new Vector(player.getPitch(),-player.getYaw(),0f);

                    Portal portal = new Portal(portalLocation, rotation, locationToTeleport, 200, 30,1.6f, 64);
                    Portal portalBack = new Portal(locationToTeleport, rotation, portalLocation, 200, 30,1.6f, 64);
                    commandSender.sendMessage(ChatUtil.formatText("Успех!"));
                }
                break;
            case 1: // player
                Player player = Bukkit.getPlayer(strings[0]);
                if (player != null) {
                    Location portalLocation = player.getLocation();
                    Location locationToTeleport = portalLocation.clone();

                    if (portalLocation.getWorld().getName().equals("null")) {
                        locationToTeleport.setWorld(WorldsManager.createNewWorld("world", World.Environment.NORMAL, WorldType.LARGE_BIOMES));
                    } else {
                        locationToTeleport.setWorld(WorldsManager.createNewWorld("null", World.Environment.NORMAL, WorldType.LARGE_BIOMES));
                    }

                    while (!locationToTeleport.getBlock().getType().isAir() && locationToTeleport.getY() < 240) {
                        locationToTeleport.add(new Vector(0,1,0));
                    }
                    locationToTeleport.add(new Vector(0f,1.5f,0f));

                    if (locationToTeleport.getY() > 239) {
                        locationToTeleport.setY(portalLocation.getY());
                    }

                    Vector rotation = new Vector(245f,0f,0f);

                    Portal portal = new Portal(portalLocation, rotation, locationToTeleport, 200, 80,1.6f, 50);
                    Portal portalBack = new Portal(locationToTeleport, rotation, portalLocation, 200, 80,1.6f, 50);

                    commandSender.sendMessage(ChatUtil.formatText("Успех!"));
                    return true;
                }
                return false;
            case 2: // size + step
                if (commandSender instanceof Player player2) {

                    float size = Float.parseFloat(strings[0]);
                    int step = Integer.parseInt(strings[1]);

                    Location portalLocation = player2.getLocation();
                    Location locationToTeleport = portalLocation.clone();

                    if (portalLocation.getWorld().getName().equals("null")) {
                        locationToTeleport.setWorld(WorldsManager.createNewWorld("world", World.Environment.NORMAL, WorldType.LARGE_BIOMES));
                    } else {
                        locationToTeleport.setWorld(WorldsManager.createNewWorld("null", World.Environment.NORMAL, WorldType.LARGE_BIOMES));
                    }

                    while (!locationToTeleport.getBlock().getType().isAir() && locationToTeleport.getY() < 240) {
                        locationToTeleport.add(new Vector(0,1,0));
                    }

                    locationToTeleport.add(new Vector(0f,1.5f,0f));

                    if (locationToTeleport.getY() > 239) {
                        locationToTeleport.setY(portalLocation.getY());
                    }
                    Vector rotation = new Vector(345f,0f,0f);

                    Portal portal = new Portal(portalLocation, rotation, locationToTeleport, 200, 80,size, step);
                    Portal portalBack = new Portal(locationToTeleport, rotation, portalLocation, 200, 80,size, step);
                    commandSender.sendMessage(ChatUtil.formatText("&bУспех!"));
                    return true;
                }
                return false;
            case 3:
                if (commandSender instanceof Player player3) {

                    Location portalLocation = getShiftedLocation(player3, 5f).add(new Vector(0,1.6f,0));
                    Location locationToTeleport = portalLocation.clone();

                    if (portalLocation.getWorld().getName().equals("null")) {
                        locationToTeleport.setWorld(WorldsManager.createNewWorld("world", World.Environment.NORMAL, WorldType.LARGE_BIOMES));
                    } else {
                        locationToTeleport.setWorld(WorldsManager.createNewWorld("null", World.Environment.NORMAL, WorldType.LARGE_BIOMES));
                    }

                    while (!locationToTeleport.getBlock().getType().isAir() && locationToTeleport.getY() < 240) {
                        locationToTeleport.add(new Vector(0,1,0));
                    }

                    locationToTeleport.add(new Vector(0f,1.5f,0f));

                    if (locationToTeleport.getY() > 239) {
                        locationToTeleport.setY(portalLocation.getY());
                    }

                    Vector rotation = new Vector(Float.parseFloat(strings[0]),Float.parseFloat(strings[1]),Float.parseFloat(strings[2]));

                    Portal portal = new Portal(portalLocation, rotation, locationToTeleport, 200, 80,1.6f, 50);
                    Portal portalBack = new Portal(locationToTeleport, rotation, portalLocation, 200, 80,1.6f, 50);
                    commandSender.sendMessage(ChatUtil.formatText("Успех!"));
                }
                break;

            default:
                commandSender.sendMessage(ChatUtil.formatText("&cНеверное количество аргументов!"));
        }

        return false;
    }

    public Location getShiftedLocation(Player player, double distance) {
        // Получаем текущую позицию игрока
        Location playerLocation = player.getLocation();

        // Получаем направление по Yaw (без Pitch)
        Vector direction = getLookVector(player).normalize().multiply(distance);
        //Bukkit.broadcastMessage(direction.toString());

        // Смещаем позицию вперёд
        return playerLocation.add(direction);
    }

    public Vector getLookVector(Player player) {
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z =  Math.cos(yawRad) * Math.cos(pitchRad);

        return new Vector(x, y, z);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        switch (strings.length) {
            case 1:
                return List.of("open","closeAll");
            default:
                return List.of();
        }
    }
}
