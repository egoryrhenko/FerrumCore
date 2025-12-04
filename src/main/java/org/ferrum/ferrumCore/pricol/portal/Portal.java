package org.ferrum.ferrumCore.pricol.portal;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.managers.CooldownManager;
import org.ferrum.ferrumCore.utils.Scheduler;

import java.util.*;

public class Portal {
    public static final float PI2 = (float) (Math.PI * 2);

    protected final Location position;
    protected final Vector portalRotation;
    protected final Location locationToTeleport;
    protected final float speed;
    protected final float radius;
    protected final int lifeTime;
    protected final int activationTime;
    protected final int closeTime;
    protected final float step;

    protected Particle frameParticle;
    protected byte state;

    protected Scheduler.Task task;
    protected static int lastId;
    public final int id;

    protected Portal(Location position, Vector portalRotation, Location locationToTeleport, float speed, float radius, int lifeTime, int activationTime, int closeTime, float step) {
        this.position = position;
        this.portalRotation = portalRotation;
        this.locationToTeleport = locationToTeleport;
        this.speed = speed;
        this.radius = radius;
        this.lifeTime = lifeTime;
        this.activationTime = activationTime;
        this.closeTime = closeTime;
        this.step = step;

        this.id = lastId;
        lastId++;
    }



    public Portal(Location position, Vector portalRotation, Location locationToTeleport,  float scale, int lifeTime, int activationTime, int closeTime, int points) {
        this(position, portalRotation, locationToTeleport,scale / 8.8f, scale, lifeTime, activationTime, closeTime, PI2 / points);

        createPortal();
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
                            close();
                            return;
                        }
                    }
                }
                ticks++;
            }
        }, 0,1);
    }

    public void close() {
        if (task != null) {
            task.cancel();
            task = null;
            PortalManager.portals.remove(this);
        }
    }

    protected void renderPortalFrame(int ticks) {

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

            position.getWorld().spawnParticle(
                    frameParticle,
                    position.getX() + point.getX(),
                    position.getY() + point.getY(),
                    position.getZ() + point.getZ(),
                    0,
                    pointVelocity.getX(),
                    pointVelocity.getY(),
                    pointVelocity.getZ(),
                    speed,
                    null,
                    true
            );
        }
    }

    protected void renderPortalFrameOpenAnimation(int ticks) {

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

            position.getWorld().spawnParticle(
                    frameParticle,
                    position.getX() + point.getX(),
                    position.getY() + point.getY(),
                    position.getZ() + point.getZ(),
                    0,
                    pointV.getX(),
                    pointV.getY(),
                    pointV.getZ(),
                    speed,
                    null,
                    true
            );
        }

    }

    protected void renderPortal(int ticks) {
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

                    position.getWorld().spawnParticle(Particle.END_ROD, position.getX() + point.getX(), position.getY() +  point.getY(), position.getZ() +  point.getZ(), 0, 0, 0, 0, speed, null, true);

                    }
                }
            }
        }

    protected static void rotateAroundX(Vector v, double angle) {
        double y = v.getY() * Math.cos(angle) - v.getZ() * Math.sin(angle);
        double z = v.getY() * Math.sin(angle) + v.getZ() * Math.cos(angle);
        v.setY(y);
        v.setZ(z);
    }

    protected static void rotateAroundY(Vector v, double angle) {
        double x = v.getX() * Math.cos(angle) + v.getZ() * Math.sin(angle);
        double z = -v.getX() * Math.sin(angle) + v.getZ() * Math.cos(angle);
        v.setX(x);
        v.setZ(z);
    }

    protected static void rotateAroundZ(Vector v, double angle) {
        double x = v.getX() * Math.cos(angle) - v.getY() * Math.sin(angle);
        double y = v.getX() * Math.sin(angle) + v.getY() * Math.cos(angle);
        v.setX(x);
        v.setY(y);
    }

    protected boolean checkPlayerTouchWithParticles(Entity player) {
        //logL(player.getLocation());
        //logL(portalPosition);
        Vector relative = player.getLocation().toVector().subtract(position.toVector());
        //FerrumCore.log(relative.getX() + ", " + relative.getY() + ", " + relative.getZ());
        // Обратное вращение портала
        rotateAroundZ(relative, -Math.toRadians(portalRotation.getZ()));
        rotateAroundY(relative, -Math.toRadians(portalRotation.getY()));
        rotateAroundX(relative, -Math.toRadians(portalRotation.getX()));

        return relative.getX() * relative.getX() + relative.getY() * relative.getY() <= radius * radius && relative.getZ() < 0.15f && relative.getZ() > -0.15f;
    }
    protected void teleportPlayers() {
        Collection<Entity> nearbyEntity = position.getNearbyEntities(radius, radius, radius);
        for (Entity entity : nearbyEntity) {

            if (!checkPlayerTouchWithParticles(entity)) {
                continue;
            }

            if (CooldownManager.isCooldown(entity.getUniqueId(),"teleport")) {
                continue;
            }

            CooldownManager.setCooldown(entity.getUniqueId(), "teleport",2000);
            Vector velocity = entity.getVelocity();
            entity.teleport(locationToTeleport);
            entity.setVelocity(velocity);
        }
    }
    protected void logLoc(Location location) {
        FerrumCore.log("[Loc] "+location.getX() + ", " + location.getY() + ", "+location.getX());
    }
}
