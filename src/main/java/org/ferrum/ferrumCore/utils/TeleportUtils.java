package org.ferrum.ferrumCore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.FerrumCore;

public final class TeleportUtils {

    public static void teleport(Entity entity, Location location) {
        if (entity == null) return;
        if (Scheduler.isFolia()) {
            // Безопасно запускаем код в регионе игрока
            Bukkit.getRegionScheduler().execute(FerrumCore.plugin, entity.getLocation(),
                    () -> entity.teleportAsync(location));
        } else {
            // Обычное поведение для Paper/Spigot
            Bukkit.getScheduler().runTask(FerrumCore.plugin,
                    () -> entity.teleport(location));
        }
    }
}