package org.ferrum.ferrumCore.pricol.portal;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.ferrum.ferrumCore.utils.Scheduler;

public class MovingPortal extends Portal {
    private final Entity target;
    private final double moveSpeed; // скорость перемещения
    public MovingPortal(Entity target, Location locationToTeleport, float scale, int lifeTime, int activationTime, int closeTime, int points, double moveSpeed) {
        super(target.getLocation().clone(), new Vector(0, 0, 0), locationToTeleport, scale, lifeTime, activationTime, closeTime, points);
        this.target = target;
        this.moveSpeed = moveSpeed;
    }


    private void createPortal() {
        task = Scheduler.runTimer(new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                switch (PortalState.forId(state)) {
                    case OPEN -> {
                        renderPortalFrameOpenAnimation(ticks);
                        if (ticks > activationTime) {
                            state++;
                        }
                    }
                    case WORK -> {
                        Location targetLoc = target.getLocation();
                        Vector direction = targetLoc.toVector().subtract(position.toVector()).normalize();

                        // сдвигаем портал по направлению к цели
                        position.add(direction.clone().multiply(moveSpeed));

                        // немного вращаем для красивого эффекта
                        portalRotation.add(new Vector(0, 10, 0));

                        // рендерим визуально
                        renderPortalFrame(ticks);
                        renderPortal(ticks);

                        // проверяем касание
                        if (checkPlayerTouchWithParticles(target)) {
                            teleportPlayers(); // телепортирует цель
                            cancel();
                            return;
                        }
                        renderPortalFrame(ticks);
                        renderPortal(ticks);
                        teleportPlayers();
                        if (ticks > closeTime) {
                            state++;
                        }
                    }
                    case CLOSE -> {
                        renderPortalFrame(ticks);
                        if (ticks > lifeTime) {
                            cancel();
                            return;
                        }
                    }
                }
                ticks++;
            }
        }, 0,1);
    }
}
