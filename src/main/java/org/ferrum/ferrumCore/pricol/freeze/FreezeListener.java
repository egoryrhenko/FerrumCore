package org.ferrum.ferrumCore.pricol.freeze;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;


import java.util.HashMap;
import java.util.Map;

import static org.ferrum.ferrumCore.pricol.portal.Portal.PI2;


public class FreezeListener implements Listener {
    public static FreezeListener freezeListener;
    public Map<Player, Location> freezePlayers = new HashMap<>();

    static {
        freezeListener = new FreezeListener();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!freezePlayers.containsKey(player)) {
            return;
        }

        Location loc = freezePlayers.get(player);

        if (loc.distanceSquared(player.getLocation()) < 4) {
            return;
        }

        if (event.getFrom().distanceSquared(event.getTo()) == 0) {
            return;
        }

        if (loc.distanceSquared(event.getTo()) < loc.distanceSquared(event.getFrom())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onServerTick(ServerTickEndEvent event) {
        for (Map.Entry<Player, Location> entry : freezePlayers.entrySet()) {
            if (!entry.getKey().isOnline()) {
                freezePlayers.remove(entry.getKey());
                if (freezePlayers.isEmpty()) {
                    HandlerList.unregisterAll(freezeListener);
                }
            }
            drowCircle(entry.getValue(), 2, PI2 / 64);
        }
    }

    public void drowCircle(Location position, float radius, float step) {
        for (float stepAngle = 0 ; stepAngle < PI2; stepAngle += step) {
            // 1. Генерируем точку круга в локальных координатах
            Vector point = new Vector(
                    radius * Math.cos(stepAngle),
                    0.1,
                    radius * Math.sin(stepAngle)
            );

            Vector pointVelocity = new Vector(
                    Math.cos(stepAngle + 90), // X
                    90, // Y
                    Math.sin(stepAngle + 90) // Z
            );

            position.getWorld().spawnParticle(
                    Particle.FLAME,
                    position.getX() + point.getX(),
                    position.getY() + point.getY(),
                    position.getZ() + point.getZ(),
                    0,
                    pointVelocity.getX(),
                    pointVelocity.getY(),
                    pointVelocity.getZ(),
                    0.01f,
                    null,
                    true
            );
        }
    }
}
