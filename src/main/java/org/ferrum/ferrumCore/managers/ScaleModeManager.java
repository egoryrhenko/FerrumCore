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
import org.bukkit.scheduler.BukkitRunnable;
import org.ferrum.ferrumCore.utils.FerrumListener;
import org.ferrum.ferrumCore.utils.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class ScaleModeManager extends FerrumListener implements CommandExecutor, TabCompleter {
    private static final float DEFAULT_SPEED = 0.005f;
    HashSet<UUID> playerSmaleScale = new HashSet<>();

    private boolean isSmale(Player player) {
        return playerSmaleScale.contains(player.getUniqueId());
    }

    private AttributeInstance getScaleAttribute(Player player) {
        return player.getAttribute(Attribute.SCALE);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player player){
            if (strings.length == 2 && player.hasPermission("ferrum.admin")) {
                AttributeInstance attribute = player.getAttribute(Attribute.SCALE);

                if (attribute == null) {
                    return true;
                }

                GrowAnimation(attribute, Float.parseFloat(strings[0]), Float.parseFloat(strings[1]));
            } else if (strings.length == 1) {
                String cooldown = CooldownManager.getCooldown(player.getUniqueId(), "size");
                if (cooldown != null) {
                    player.sendMessage("Вы сможете изменить рост только через " + cooldown);
                    return true;
                }

                float value = Float.parseFloat(strings[0]);

                if (value > 1.2f) {
                    player.sendMessage(Component.text("Максимальное значение 1.2"));
                }
                if (value < 0.8f) {
                    player.sendMessage(Component.text("Минимальное значение 0.8"));
                }
                if (value != 1f) {
                    playerSmaleScale.add(player.getUniqueId());
                }

                GrowAnimation(getScaleAttribute(player), Math.max(0.8f, Math.min(1.2f, value)), DEFAULT_SPEED);
            } else {
                String cooldown = CooldownManager.getCooldown(player.getUniqueId(), "size");
                if (cooldown != null) {
                    player.sendMessage("Вы сможете изменить рост только через " + cooldown);
                    return true;
                }
                if (isSmale(player)){
                    playerSmaleScale.remove(player.getUniqueId());
                    GrowAnimation(getScaleAttribute(player),1f, DEFAULT_SPEED);
                } else {
                    playerSmaleScale.add(player.getUniqueId());
                    GrowAnimation(getScaleAttribute(player), 0.8f, DEFAULT_SPEED);
                }
            }
        }
        return true;
    }


    @EventHandler
    public void PlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager) {

            if (event.getDamage() < 2d) {
                return;
            }
            if (isSmale(damager)) {
                playerSmaleScale.remove(damager.getUniqueId());
                getScaleAttribute(damager).setBaseValue(1f);
            }
            CooldownManager.setCooldown(damager.getUniqueId(), "size", ConfigManager.getIntByKey("size_cooldown"));

            if (event.getEntity() instanceof Player player){
                if (isSmale(player)){
                    playerSmaleScale.remove(damager.getUniqueId());
                    getScaleAttribute(player).setBaseValue(1f);
                }
                CooldownManager.setCooldown(damager.getUniqueId(), "size", ConfigManager.getIntByKey("size_cooldown"));
                CooldownManager.setCooldown(player.getUniqueId(), "size", ConfigManager.getIntByKey("size_cooldown"));
            }
        }
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if (isSmale(player)) {
            playerSmaleScale.remove(player.getUniqueId());
            getScaleAttribute(player).setBaseValue(1f);
        }
    }
    private void GrowAnimation(AttributeInstance attribute, float EndValue, float speed){
        if (speed <= 0) return;
        Scheduler.runTimer(new BukkitRunnable() {
            private float size = (float) attribute.getBaseValue();
            private final boolean isGrow = size < EndValue;

            @Override
            public void run() {
                if (isGrow) {
                    size += speed;
                    if (size >=EndValue) {
                        size = EndValue;
                        this.cancel();
                    }
                    attribute.setBaseValue(size);
                } else {
                    size -= speed;
                    if (size <=EndValue){
                        size = EndValue;
                        this.cancel();
                    }
                    attribute.setBaseValue(size);
                }

            }
        },0,1);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 1) {
            return List.of("[size]");
        }
        return List.of();
    }
}