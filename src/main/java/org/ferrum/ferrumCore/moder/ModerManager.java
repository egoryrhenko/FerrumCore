package org.ferrum.ferrumCore.moder;

import io.papermc.paper.entity.Shearable;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.FerrumListener;
import org.ferrum.ferrumCore.utils.Scheduler;
import org.ferrum.ferrumCore.utils.TeleportUtils;

import java.util.HashMap;

public class ModerManager extends FerrumListener {

    public static HashMap<Player, ModerData> activeModerData = new HashMap<>();

    public static boolean inModerMod(Player player) {
        return activeModerData.containsKey(player);
    }

    public static void enableModerMod(Player player) {
        Scheduler.run(() -> {
            FerrumCore.runCommandNotSafe("irp forcebackup player " + player.getName());
            activeModerData.put(player, new ModerData(player.getLocation(), player.getInventory().getContents()));
        });
        Bukkit.getRegionScheduler().execute(FerrumCore.plugin, player.getLocation(), () -> {
            player.getInventory().clear();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, -1, 0, false, false));
            player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(0.6f);
            player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(10f);
        });
    }

    public static void disableModerMod(Player player) {
        Bukkit.getRegionScheduler().execute(FerrumCore.plugin, player.getLocation(), () -> {
            ModerData moderData = activeModerData.get(player);
            TeleportUtils.teleportNotSafe(player, moderData.location());
            player.getInventory().setContents(moderData.inventory());
            player.setGameMode(GameMode.SURVIVAL);
            activeModerData.remove(player);
            player.removePotionEffect(PotionEffectType.SATURATION);
            player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(0f);
            player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(4.5f);
        });
    }


    public static void kickAllModerMod() {
        for (Player player : activeModerData.keySet()) {
            disableModerMod(player);
        }
    }


    @EventHandler
    public void attackModer(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (activeModerData.containsKey(player)) {
                event.setDamage(0);
                player.setFireTicks(0);
            }
        }

    }

    @EventHandler
    public void PlayerAttackModer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) {
            return;
        }
        if (damager.hasPermission("ferrum.moder")) return;

        if (event.getEntity() instanceof Player moder) {
            if (activeModerData.containsKey(moder)) {
                damager.sendRichMessage("Не атакуйте и не мешайте модератору работать");
            }
        }

    }
}
