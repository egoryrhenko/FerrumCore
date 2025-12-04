package org.ferrum.ferrumCore.pricol.anime;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InfinityVoid implements Listener, CommandExecutor {

    private final Set<Player> infinityPlayers = new HashSet<>();
    private final Map<UUID, Set<Entity>> frozenEntities = new HashMap<>();
    private Scheduler.Task infinityTask;

    private static final double RADIUS_XZ_INNER = 1.2;
    private static final double RADIUS_XZ_OUTER = 2.4;
    private static final double RADIUS_Y_INNER = 1.8;
    private static final double RADIUS_Y_OUTER = 2.4;


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cЭта команда только для игроков.");
            return true;
        }

        if (infinityPlayers.contains(player)) {
            disableInfinity(player);
            player.sendMessage("§c[∞] Эффект бесконечности выключен.");
        } else {
            enableInfinity(player);
            player.sendMessage("§b[∞] Эффект бесконечности включён.");
        }

        return true;
    }

    private void enableInfinity(Player player) {
        infinityPlayers.add(player);
        frozenEntities.put(player.getUniqueId(), new HashSet<>());

        if (infinityTask == null) startInfinityTask();
    }

    private void disableInfinity(Player player) {
        infinityPlayers.remove(player);

        Set<Entity> frozen = frozenEntities.remove(player.getUniqueId());
        if (frozen != null) frozen.forEach(this::unfreezeEntity);

        if (infinityTask != null && infinityPlayers.isEmpty() ) {
            infinityTask.cancel();
            infinityTask = null;
        }
    }

    private void startInfinityTask() {
        infinityTask = Scheduler.runTimer(new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : infinityPlayers){
                    if (!player.isOnline()) {
                        infinityPlayers.remove(player);
                        continue;
                    }
                    Set<Entity> stoppedEntity = frozenEntities.get(player.getUniqueId());
                    Location location = player.getLocation();
                    location.add(0f,0.9f,0f);

                    Set<Entity> nearbyEntities = (Set<Entity>) location.getWorld().getNearbyEntities(location,RADIUS_XZ_INNER,RADIUS_Y_INNER,RADIUS_XZ_INNER);
                    Set<Entity> outerRadiusEntities = (Set<Entity>) location.getWorld().getNearbyEntities(location,RADIUS_XZ_OUTER,RADIUS_Y_OUTER,RADIUS_XZ_INNER);
                    nearbyEntities.remove(player);
                    outerRadiusEntities.remove(player);
                    for (Entity entity : outerRadiusEntities) {
                        if (entity instanceof Arrow arrow) {
                            if (isTest(arrow, player.getLocation())) {
                                FerrumCore.log("skip");
                                continue;
                            }
                            FerrumCore.log(arrow.toString());
                            FerrumCore.log(arrow.getLocation().toString());
                            FerrumCore.log(arrow.getVelocity().toString());
                            FerrumCore.log(arrow.getVelocity().lengthSquared()+"");
                            if (arrow.getVelocity().length() > 0.4f) arrow.setVelocity(arrow.getVelocity().multiply(0.5f));
                            arrow.getWorld().spawnParticle(
                                    Particle.FLAME,
                                    arrow.getLocation().getX(),
                                    arrow.getLocation().getY(),
                                    arrow.getLocation().getZ(),
                                    0,
                                    0d,
                                    0d,
                                    0d,
                                    0,
                                    null,
                                    true);
                            arrow.getWorld().spawnParticle(
                                    Particle.SOUL_FIRE_FLAME,
                                    arrow.getLocation().getX() + arrow.getVelocity().getX(),
                                    arrow.getLocation().getY()+ arrow.getVelocity().getY(),
                                    arrow.getLocation().getZ()+ arrow.getVelocity().getZ(),
                                    0,
                                    0d,
                                    0d,
                                    0d,
                                    0,
                                    null,
                                    true);
                        }
                    }
                    nearbyEntities.forEach(e -> freezeEntity(e));
                    nearbyEntities.forEach(stoppedEntity::remove);
                    stoppedEntity.forEach(e -> unfreezeEntity(e));
                    frozenEntities.put(player.getUniqueId(), nearbyEntities);
                }
            }
        },0,1);
    }

    public static boolean isTest(Entity entity, Location location) {
        return location.distance(entity.getLocation().add(entity.getVelocity())) > location.distance(entity.getLocation());
    }

    private void freezeEntity(Entity entity) {
        entity.setGravity(false);


        if (entity instanceof Mob mob) {
            mob.setAware(false);
            //mob.setTarget(null);
            mob.setSilent(true);
        }
    }

    private void unfreezeEntity(Entity entity) {
        if (entity.isDead()) return;
        entity.setGravity(true);

        if (entity instanceof Mob mob) {
            mob.setAware(true);
            mob.setSilent(false);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!infinityPlayers.contains(victim)) return;
        event.setCancelled(true);

        Entity damager = event.getDamager();
        if (damager instanceof Player attacker) {
            spawnDisplayWeapon(attacker, victim);
        } else if (damager instanceof Projectile proj) {
            proj.setVelocity(new Vector(0, 0, 0));
        }
    }

    private void spawnDisplayWeapon(Player attacker, Player victim) {
        ItemStack item = attacker.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) return;

        Vector dir = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize();
        Location spawnLoc = victim.getLocation().add(dir.multiply(1.2)).add(0, 1.0, 0);

        ItemDisplay display = (ItemDisplay) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ITEM_DISPLAY);
        display.setItemStack(item.clone());
        attacker.getInventory().setItemInMainHand(null);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (attacker.isOnline()) {
                    attacker.getInventory().addItem(item);
                }
                display.remove();
            }
        }.runTaskLater(FerrumCore.plugin, 60L);
    }
}
