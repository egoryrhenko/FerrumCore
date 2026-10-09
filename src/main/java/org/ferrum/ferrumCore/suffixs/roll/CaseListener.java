package org.ferrum.ferrumCore.suffixs.roll;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.FerrumListener;
import org.ferrum.ferrumCore.utils.Scheduler;
import org.jetbrains.annotations.Async;

public class CaseListener extends FerrumListener {


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if (inv != null && inv.getHolder() instanceof CaseMenu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (!(inv.getHolder() instanceof CaseMenu caseMenu)) return;

        if (caseMenu.animationTicks < 1) {
            return;
        }

        if (caseMenu.animationTicks > 20) {
            caseMenu.stop();
            return;
        }

        Player player = (Player) event.getPlayer();

        switch (event.getReason()) {
            case DISCONNECT, DEATH -> {
                caseMenu.forceFinish(player);
            }
            default -> {
                Scheduler.run(() -> CaseMenu.openSuffixMenu(player));
            }
        }
    }
}
