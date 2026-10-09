package org.ferrum.ferrumCore.pricol.anime;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.Scheduler;
import org.ferrum.ferrumCore.utils.TeleportUtils;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Charge {
    private final List<Vector> sphereOffsets = new ArrayList<>(); // заранее посчитанные смещения

    private final int killRadius;
    private final float sphereRadius;
    private final int maxLifeTime;
    private final int activationTime;
    private final float damage;
    private final float speed;
    private ItemDisplay model;
    public Location location;

    private Scheduler.Task task;

    public Charge(Location location, Material material, float sphereRadius,
                  int lifeTime, int activationTime, float damage, float speed) {

        this.killRadius = (int) (sphereRadius / 2);
        this.sphereRadius = sphereRadius;
        this.maxLifeTime = lifeTime;
        this.activationTime = activationTime;
        this.damage = damage;
        this.speed = speed;
        this.location = location;

        precomputeSphereOffsets(sphereRadius);
        spawnSphereEntity(material);
        startMoveSphereEntity();
    }


    public void stop() {
        model.remove();
        if (task != null) {
            task.cancel();
        }
        ChargeManager.charges.remove(this);
    }

    /**
     * Вычисляем оффсеты блоков в радиусе один раз
     */
    private void precomputeSphereOffsets(float radius) {
        float r2 = radius * radius;
        for (float x = -radius; x <= radius; x++) {
            for (float y = -radius; y <= radius; y++) {
                for (float z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= r2) {
                        sphereOffsets.add(new Vector(x, y, z));
                    }
                }
            }
        }
    }

    private void spawnSphereEntity(Material material) {
        model = location.getWorld().spawn(location, ItemDisplay.class);
        model.setItemStack(new ItemStack(material));
        model.setTransformationMatrix(new Matrix4f().scale(sphereRadius));
        model.setTeleportDuration(1);
    }

    private void startMoveSphereEntity() {
       task = Scheduler.runRegionTimer(location,new Runnable() {
            private float angle = 0;
            private float pitch = 0;
            private int lifetime = 0;
            private final Location currentLocation = model.getLocation();
            private final Vector step = location.getDirection().normalize().multiply(speed);

            @Override
            public void run() {
                if (lifetime >= maxLifeTime) {
                    stop();
                    return;
                }

                double length = step.length();
                int steps = (int) Math.ceil(length * 2);
                Vector smallStep = step.clone().multiply(1.0 / steps);

                for (int i = 0; i < steps; i++) {
                    currentLocation.add(smallStep);
                    if (lifetime > activationTime) {
                        damageNearbyEntities(currentLocation);
                        breakBlocksAt(currentLocation);
                    }
                }

                angle = getAngle(angle, 23);
                pitch = getPitch(pitch, 23);

                TeleportUtils.teleportNotSafe(model, currentLocation);
                model.setRotation(angle, pitch);

                lifetime++;
            }
        },0,1L);
    }


    private void damageNearbyEntities(Location location) {
        if (location == null) return;
        World world = location.getWorld();
        if (world == null) return;

        world.getNearbyEntities(location, killRadius, killRadius, killRadius)
                .forEach(entity -> {
                    if (entity instanceof LivingEntity living && living.isValid()) {
                        living.damage((living.getHealth() * damage) + 1f);
                    }
                });
    }

    private void breakBlocksAt(Location center) {
        World world = center.getWorld();
        for (Vector offset : sphereOffsets) {
            Block block = world.getBlockAt(
                    center.getBlockX() + offset.getBlockX(),
                    center.getBlockY() + offset.getBlockY(),
                    center.getBlockZ() + offset.getBlockZ()
            );
            if (!block.getType().isAir()) {
                BreakManager.logBlock(block, 300);
                block.setType(Material.AIR, false);
            }
        }
    }

    public float getAngle(float angle, float delta) {
        return (angle + delta) % 360;
    }

    public float getPitch(float pitch, float delta) {
        pitch += delta;
        if (pitch >= 90) pitch -= 180;
        return pitch;
    }
}
