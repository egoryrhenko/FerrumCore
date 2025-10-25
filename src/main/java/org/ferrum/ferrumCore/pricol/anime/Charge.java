//package org.ferrum.ferrumCore.pricol.anime;
//
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.World;
//import org.bukkit.block.Block;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.ItemDisplay;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.scheduler.BukkitRunnable;
//import org.bukkit.util.Vector;
//import org.ferrum.ferrumCore.FerrumCore;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Charge {
//    private final List<Vector> sphereOffsets = new ArrayList<>(); // заранее посчитанные смещения
//
//    private final int killRadius;
//    private final float sphereRadius;
//    private final int maxLifeTime;
//    private final int activationTime;
//    private final float damage;
//    private final float speed;
//    private ItemDisplay model;
//
//    public Charge(Location location, Material material, float sphereRadius,
//                  int lifeTime, int activationTime, float damage, float speed) {
//
//        this.killRadius = (int) (sphereRadius / 2);
//        this.sphereRadius = sphereRadius;
//        this.maxLifeTime = lifeTime;
//        this.activationTime = activationTime;
//        this.damage = damage;
//        this.speed = speed;
//
//        precomputeSphereOffsets(breakeRadius);
//        spawnSphereEntity(player);
//    }
//
//    /**
//     * Вычисляем оффсеты блоков в радиусе один раз
//     */
//    private void precomputeSphereOffsets(int radius) {
//        int r2 = radius * radius;
//        for (int x = -radius; x <= radius; x++) {
//            for (int y = -radius; y <= radius; y++) {
//                for (int z = -radius; z <= radius; z++) {
//                    if (x * x + y * y + z * z <= r2) {
//                        sphereOffsets.add(new Vector(x, y, z));
//                    }
//                }
//            }
//        }
//    }
//
//    private void spawnSphereEntity(Player player) {
//        Location location = player.getLocation().add(0, 1, 0);
//
//        ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class);
//        itemDisplay.setItemStack(new ItemStack());
//        itemDisplay.setTeleportDuration(1);
//
//        spawnSphereEntity(itemDisplay, player);
//    }
//
//    private void spawnSphereEntity(Location location, Material material) {
//        new BukkitRunnable() {
//            private float angle = 0;
//            private float pitch = 0;
//            private int lifetime = 0;
//            private Location currentLocation = model.getLocation();
//            private final org.bukkit.util.Vector step = player.getLocation().getDirection().normalize().multiply(speed);
//
//            @Override
//            public void run() {
//                if (lifetime >= maxLifeTime) {
//                    model.remove();
//                    this.cancel();
//                    return;
//                }
//
//                // Разбиваем движение на маленькие шаги, чтобы не пропускать блоки
//                double length = step.length();
//                int steps = (int) Math.ceil(length * 2); // шаг каждые 0.5 блока
//                org.bukkit.util.Vector smallStep = step.clone().multiply(1.0 / steps);
//
//                for (int i = 0; i < steps; i++) {
//                    currentLocation.add(smallStep);
//
//                    if (lifetime > activationTime) {
//                        damageNearbyEntities(currentLocation);
//                        breakBlocksAt(currentLocation);
//                    }
//                }
//
//                angle = getAngle(angle, 23);
//                pitch = getPitch(pitch, 23);
//                model.teleport(currentLocation);
//                model.setRotation(angle, pitch);
//
//                lifetime++;
//            }
//        }.runTaskTimer(FerrumCore.plugin, 0L, 1L);
//    }
//
//    private void damageNearbyEntities(Location location) {
//        for (Entity entity : location.getWorld().getNearbyEntities(location, killRadius, killRadius, killRadius)) {
//            if (entity instanceof LivingEntity living) {
//                living.damage(damage);
//            }
//        }
//    }
//
//    private void breakBlocksAt(Location center) {
//        World world = center.getWorld();
//        for (Vector offset : sphereOffsets) {
//            Block block = world.getBlockAt(
//                    center.getBlockX() + offset.getBlockX(),
//                    center.getBlockY() + offset.getBlockY(),
//                    center.getBlockZ() + offset.getBlockZ()
//            );
//            if (!block.getType().isAir()) {
//                BreakManager.logBlock(block, 300);
//                block.setType(Material.AIR, false);
//            }
//        }
//    }
//
//    public float getAngle(float angle, float delta) {
//        return (angle + delta) % 360;
//    }
//
//    public float getPitch(float pitch, float delta) {
//        pitch += delta;
//        if (pitch >= 90) pitch -= 180;
//        return pitch;
//    }
//}
