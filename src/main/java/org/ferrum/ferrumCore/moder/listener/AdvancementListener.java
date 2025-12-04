package org.ferrum.ferrumCore.moder.listener;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import org.bukkit.Bukkit;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ferrum.ferrumCore.moder.ModerManager;
import org.ferrum.ferrumCore.utils.FerrumListener;

public class AdvancementListener extends FerrumListener {

    @EventHandler
    public void onPlayerLeave(PlayerAdvancementCriterionGrantEvent event) {
        Player player = event.getPlayer();
        if (!(player.getGameMode().name().equals("SURVIVAL")) || ModerManager.inModerMod(player)) {
            event.setCancelled(true);
        }
    }
}
