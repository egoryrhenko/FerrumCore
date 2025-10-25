package org.ferrum.ferrumCore.moder;

import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ferrum.ferrumCore.FerrumCore;

import java.util.HashMap;

public class ModerManager implements Listener {

    public static HashMap<Player, ModerData> activeModerData = new HashMap<>();

    public static boolean inModerMod(Player player) {
        return activeModerData.containsKey(player);
    }

    public static void enableModerMod(Player player) {
        FerrumCore.runCommand("irp forcebackup player " + player.getName());
        activeModerData.put(player, new ModerData(player.getLocation(), player.getInventory().getContents()));
        player.getInventory().clear();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, -1,0,false, false));
        player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(0.6f);
    }

    public static void disableModerMod(Player player) {
        ModerData moderData = activeModerData.get(player);
        player.teleport(moderData.location);
        player.getInventory().setContents(moderData.inventory);
        player.setGameMode(GameMode.SURVIVAL);
        activeModerData.remove(player);
        player.removePotionEffect(PotionEffectType.SATURATION);
        player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(0f);
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
}
