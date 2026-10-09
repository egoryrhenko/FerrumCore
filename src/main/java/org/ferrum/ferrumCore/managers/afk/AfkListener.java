package org.ferrum.ferrumCore.managers.afk;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ferrum.ferrumCore.utils.FerrumListener;

public class AfkListener extends FerrumListener {


    private final AfkManager afkManage;

    public AfkListener(AfkManager afkManage) {
        this.afkManage = afkManage;
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if (!afkManage.afk.contains(event.getPlayer().getUniqueId())) return;

        Player player = event.getPlayer();

        afkManage.afk.remove(player.getUniqueId());
        afkManage.sendAfkEndMessage(player);
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        afkManage.remove(event.getPlayer().getUniqueId());
    }
}
