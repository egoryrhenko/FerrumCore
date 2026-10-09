package org.ferrum.ferrumCore.managers;

import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ferrum.ferrumCore.utils.FerrumListener;
import org.ferrum.ferrumCore.utils.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class ScaleModeManager extends FerrumListener implements CommandExecutor, TabCompleter {

    private static final float DEFAULT_SPEED = 0.005f;
    private static final HashSet<UUID> playerSmaleScale = new HashSet<>();
    private boolean isSmale(Player player) {
        return playerSmaleScale.contains(player.getUniqueId());
    }

    private AttributeInstance getScaleAttribute(Player player) {
        return player.getAttribute(Attribute.SCALE);
    }

    /* ================= COMMAND ================= */

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {

        if (!(sender instanceof Player player)) return true;

        if (args.length == 2 && player.hasPermission("ferrum.admin")) {
            AttributeInstance attribute = getScaleAttribute(player);
            if (attribute == null) return true;

            GrowAnimation(attribute,
                    Float.parseFloat(args[0]),
                    Float.parseFloat(args[1]));
            return true;
        }

        if (args.length == 1) {
            String cooldown = CooldownManager.getCooldown(player.getUniqueId(), "size");
            if (cooldown != null) {
                player.sendMessage("Вы сможете изменить рост только через " + cooldown);
                return true;
            }
            try {
                float value = Float.parseFloat(args[0]);

                if (!player.hasPermission("ferrum.admin")) {
                    if (player.hasPermission("ferrum.size.big-limit")) {
                        if (value > 1.6f) {
                            player.sendMessage("Максимальный размер 1.6");
                        }
                        if (value < 0.6f) {
                            player.sendMessage("Минимальный размер 0.6");
                        }
                        value = Math.clamp(value, 0.6f, 1.6f);
                    } else {
                        if (value > 1.2f) {
                            player.sendMessage("Максимальный размер 1.2");
                        }
                        if (value < 0.8f) {
                            player.sendMessage("Минимальный размер 0.8");
                        }
                        value = Math.clamp(value, 0.8f, 1.2f);
                    }
                }


                if (value != 1f) {
                    playerSmaleScale.add(player.getUniqueId());
                } else {
                    playerSmaleScale.remove(player.getUniqueId());
                }

                GrowAnimation(getScaleAttribute(player), value, DEFAULT_SPEED);
            } catch (NumberFormatException ex) {
                player.sendRawMessage("<red>Неверный формат числа");
            }
            return true;
        }

        String cooldown = CooldownManager.getCooldown(player.getUniqueId(), "size");
        if (cooldown != null) {
            player.sendMessage("Вы сможете изменить рост только через " + cooldown);
            return true;
        }

        if (isSmale(player)) {
            playerSmaleScale.remove(player.getUniqueId());
            GrowAnimation(getScaleAttribute(player), 1f, DEFAULT_SPEED);
        } else {
            playerSmaleScale.add(player.getUniqueId());
            GrowAnimation(getScaleAttribute(player), 0.8f, DEFAULT_SPEED);
        }

        return true;
    }

    /* ================= EVENTS ================= */

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player damager)) return;
        if (event.getDamage() < 2d) return;

        if (isSmale(damager)) {
            playerSmaleScale.remove(damager.getUniqueId());
            getScaleAttribute(damager).setBaseValue(1f);
        }

        CooldownManager.setCooldown(
                damager.getUniqueId(),
                "size",
                ConfigManager.getIntByKey("size_cooldown")
        );

        if (event.getEntity() instanceof Player player) {

            if (isSmale(player)) {
                playerSmaleScale.remove(player.getUniqueId());
                getScaleAttribute(player).setBaseValue(1f);
            }

            CooldownManager.setCooldown(
                    player.getUniqueId(),
                    "size",
                    ConfigManager.getIntByKey("size_cooldown")
            );
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (isSmale(player)) {
            playerSmaleScale.remove(player.getUniqueId());
            getScaleAttribute(player).setBaseValue(1f);
        }
    }

    /* ================= ANIMATION ================= */

    private void GrowAnimation(AttributeInstance attribute, float endValue, float speed) {
        if (attribute == null || speed <= 0) return;

        final boolean grow = attribute.getBaseValue() < endValue;

        Scheduler.runWhile(() -> {
            double value = attribute.getBaseValue();

            if (grow) {
                value += speed;
                if (value >= endValue) {
                    attribute.setBaseValue(endValue);
                    return false; // stop
                }
            } else {
                value -= speed;
                if (value <= endValue) {
                    attribute.setBaseValue(endValue);
                    return false; // stop
                }
            }

            attribute.setBaseValue(value);
            return true; // continue
        }, 0, 1);
    }

    /* ================= TAB ================= */

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 1) {
            return List.of("[size]");
        }
        return List.of();
    }
}