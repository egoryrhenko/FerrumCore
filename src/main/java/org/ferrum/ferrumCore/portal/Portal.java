package org.ferrum.ferrumCore.portal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.managers.CooldownManager;
import org.joml.Matrix3d;
import org.joml.Vector3d;

import java.util.*;

public class Portal {
    private static final float PI2 = (float) (Math.PI * 2);

    private final Location portalPosition;
    private final Vector portalRotation;
    private final Location locationToTeleport;
    private final float speed;
    private final float radius;
    private final int lifeTime;
    private final int activationTime;
    private final float step;

    private BukkitTask task;
    private static int lastId;
    public final int id;


    public Portal(Location portalPosition, Vector portalRotation, Location locationToTeleport, int lifeTime, int activationTime, float scale, int points) {
        this.portalPosition = portalPosition;
        this.portalRotation = portalRotation;
        this.locationToTeleport = locationToTeleport;

        this.lifeTime = lifeTime;
        this.activationTime = activationTime;
        this.speed = scale / 8.8f;
        this.radius = scale;
        this.step = PI2 / points;

        id = lastId;
        lastId++;

        createPortal();
    }

    private void createPortal() {
        task = new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks > lifeTime) {
                    cancel();
                    return;
                }
                if (ticks < activationTime) {
                    renderPortalFrameOpenAnimation(ticks);
                } else {
                    renderPortalFrame(ticks);
                    if (lifeTime - ticks > activationTime) {
                        renderPortal(ticks);
                        teleportPlayers();
                    }
                }


                teleportPlayers();
                ticks++;
            }
        }.runTaskTimer(FerrumCore.plugin, 0, 1);
    }

    public void close() {
        task.cancel();
    }

    private void renderPortalFrame(int ticks) {
        final float innerRadius = radius * 0.88f;
        double angleX = Math.toRadians(portalRotation.getX()); // Угол по X
        double angleY = Math.toRadians(portalRotation.getY()); // Угол по Y
        double angleZ = Math.toRadians(portalRotation.getZ()); // Угол по Z

        for (float stepAngle = 0 - ((step/2)*ticks % 2); stepAngle < PI2; stepAngle += step) {
            // 1. Генерируем точку круга в локальных координатах
            Vector point = new Vector(
                    radius * Math.cos(stepAngle), // X
                    radius * Math.sin(stepAngle), // Y
                    0 // Z
            );

            Vector pointVelocity = new Vector(
                    Math.cos(stepAngle + 90), // X
                    Math.sin(stepAngle + 90), // Y
                    0 // Z
            );

            rotateAroundX(point, angleX);
            rotateAroundY(point, angleY);
            rotateAroundZ(point, angleZ);

            rotateAroundX(pointVelocity, angleX);
            rotateAroundY(pointVelocity, angleY);
            rotateAroundZ(pointVelocity, angleZ);

            // 3. Конвертируем в мировые координаты
            double x = portalPosition.getX() + point.getX();
            double y = portalPosition.getY() + point.getY(); //Переделать
            double z = portalPosition.getZ() + point.getZ();

//            double xo = portalPosition.getX() + pointOrig.getX();
//            double yo = portalPosition.getY() + pointOrig.getY(); //Переделать
//            double zo = portalPosition.getZ() + pointOrig.getZ();

            // 4. Спавним частицу
            portalPosition.getWorld().spawnParticle(Particle.FLAME, x, y, z, 0, pointVelocity.getX(), pointVelocity.getY(), pointVelocity.getZ(), speed, null, true);
            //portalPosition.getWorld().spawnParticle(Particle.CRIT, xo, yo, zo, 0, pointVelocityOrig.getX(), pointVelocityOrig.getY(), pointVelocityOrig.getZ(), speed, null, true);
        }
        //portalPosition.getWorld().spawnParticle(Particle.BUBBLE, portalPosition.getX(), portalPosition.getY(), portalPosition.getZ(), 0, 0, 0, 0, 0, null, true);
    }

    private void renderPortalFrameOpenAnimation(int ticks) {

        double angleX = Math.toRadians(portalRotation.getX()); // Угол по X
        double angleY = Math.toRadians(portalRotation.getY()); // Угол по Y
        double angleZ = Math.toRadians(portalRotation.getZ()); // Угол по Z

        //float stepAngle = (float) Math.toRadians(20) * (ticks % 360);
        for (float angel = 0f; angel < ( PI2 * ((float) ticks /activationTime)); angel += step) {
            float angelOffset = angel + (step * ticks * 3);
            // 1. Генерируем точку круга в локальных координатах
            Vector point = new Vector(
                    radius * Math.cos(angelOffset), // X
                    radius * Math.sin(angelOffset), // Y
                    0 // Z
            );

            Vector pointV = new Vector(
                    Math.cos(angelOffset + 90), // X
                    Math.sin(angelOffset + 90), // Y
                    0 // Z
            );

            // 2. Применяем вращение вокруг осей X, Y, Z
            rotateAroundX(point, angleX);
            rotateAroundY(point, angleY);
            rotateAroundZ(point, angleZ);

            rotateAroundX(pointV, angleX);
            rotateAroundY(pointV, angleY);
            rotateAroundZ(pointV, angleZ);

            // 3. Конвертируем в мировые координаты
            double x = portalPosition.getX() + point.getX();
            double y = portalPosition.getY() + point.getY(); //Переделать
            double z = portalPosition.getZ() + point.getZ();

            // 4. Спавним частицу
            portalPosition.getWorld().spawnParticle(Particle.FLAME, x, y, z, 0, pointV.getX(), pointV.getY(), pointV.getZ(), speed, null, true);
        }

    }

    private void renderPortal(int ticks) {
        float innerRadius = (radius * .88f) + (0.1f * (ticks % 2));

        double angleX = Math.toRadians(portalRotation.getX()); // Угол по X
        double angleY = Math.toRadians(portalRotation.getY()); // Угол по Y
        double angleZ = Math.toRadians(portalRotation.getZ()); // Угол по Z

        for (double x = -innerRadius; x <= innerRadius; x += 0.2) {
            for (double z = -innerRadius; z <= innerRadius; z += 0.2) {
                if (x * x + z * z <= innerRadius * innerRadius) {

                    Vector point = new Vector(
                            x, // X
                            z, // Y
                            0 // Z
                    );

                    rotateAroundX(point, angleX);
                    rotateAroundY(point, angleY);
                    rotateAroundZ(point, angleZ);

                    portalPosition.getWorld().spawnParticle(Particle.END_ROD, portalPosition.getX() + point.getX(), portalPosition.getY() +  point.getY(), portalPosition.getZ() +  point.getZ(), 0, 0, 0, 0, speed, null, true);

                    }
                }
            }
        }

    private static void rotateAroundX(Vector v, double angle) {
        double y = v.getY() * Math.cos(angle) - v.getZ() * Math.sin(angle);
        double z = v.getY() * Math.sin(angle) + v.getZ() * Math.cos(angle);
        v.setY(y);
        v.setZ(z);
    }

    private static void rotateAroundY(Vector v, double angle) {
        double x = v.getX() * Math.cos(angle) + v.getZ() * Math.sin(angle);
        double z = -v.getX() * Math.sin(angle) + v.getZ() * Math.cos(angle);
        v.setX(x);
        v.setZ(z);
    }

    private static void rotateAroundZ(Vector v, double angle) {
        double x = v.getX() * Math.cos(angle) - v.getY() * Math.sin(angle);
        double y = v.getX() * Math.sin(angle) + v.getY() * Math.cos(angle);
        v.setX(x);
        v.setY(y);
    }

    public boolean checkPlayerTouchWithParticles(Entity player) {
        //logL(player.getLocation());
        //logL(portalPosition);
        Vector relative = player.getLocation().toVector().subtract(portalPosition.toVector());
        //FerrumCore.log(relative.getX() + ", " + relative.getY() + ", " + relative.getZ());
        // Обратное вращение портала
        rotateAroundZ(relative, -Math.toRadians(portalRotation.getZ()));
        rotateAroundY(relative, -Math.toRadians(portalRotation.getY()));
        rotateAroundX(relative, -Math.toRadians(portalRotation.getX()));

        // Расстояние по плоскости XY
        double distanceSquared = relative.getX() * relative.getX() + relative.getY() * relative.getY();

        boolean inRing = distanceSquared <= radius * radius && relative.getZ() < 0.15f && relative.getZ() > -0.15f;
//        Particle particleType = inRing ? Particle.HAPPY_VILLAGER : Particle.ANGRY_VILLAGER;
//
//        // Спавним частицу на позиции игрока для наглядности
//        portalPosition.getWorld().spawnParticle(particleType,
//                player.getLocation().getX(),
//                player.getLocation().getY(),
//                player.getLocation().getZ(),
//                1, 0, 0, 0, 0, null, true);
        return inRing;
    }
    private void teleportPlayers() {
        Collection<Entity> nearbyEntity = portalPosition.getNearbyEntities(radius, radius, radius);
        for (Entity entity : nearbyEntity) {

            if (!checkPlayerTouchWithParticles(entity)) {
                continue;
            }

            if (CooldownManager.isCooldown(entity.getUniqueId(),"teleport")) {
                continue;
            }

            //if (cooldown.containsKey(entity.getUniqueId())) if (ticks - cooldown.get(entity.getUniqueId()) < 60) continue;
            /*
            switch (side) {
                case "x":
                    if (Math.abs(portalPosition.getX() - entity.getLocation().getX()) > 0.2f) continue;
                case "y":
                    if (Math.abs(portalPosition.getY() - entity.getLocation().getY()) > 0.2f) continue;
                case "z":
                    if (Math.abs(portalPosition.getZ() - entity.getLocation().getZ()) > 0.2f) continue;
            }
             */
            CooldownManager.setCooldown(entity.getUniqueId(), "teleport",2000);
            entity.teleport(locationToTeleport);
        }
    }
    private void logL(Location location) {
        FerrumCore.log("[Loc] "+location.getX() + ", " + location.getY() + ", "+location.getX());
    }
}
