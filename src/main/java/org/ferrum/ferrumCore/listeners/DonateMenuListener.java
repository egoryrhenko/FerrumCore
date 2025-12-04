package org.ferrum.ferrumCore.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.ferrum.ferrumCore.chat.util.DonatItem;
import org.ferrum.ferrumCore.hooks.LuckPermsHook;
import org.ferrum.ferrumCore.managers.DonateManager;
import org.ferrum.ferrumCore.managers.save.SuffixData;
import org.ferrum.ferrumCore.utils.FerrumListener;

public class DonateMenuListener extends FerrumListener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!LuckPermsHook.isLuckPerms()) return;

        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && event.getView().title().equals(DonateManager.getTitle())) {
            Player player = (Player) event.getWhoClicked();

            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR || clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                return;
            }

            if (clickedItem.getType() == Material.RED_STAINED_GLASS_PANE) {
                SuffixData.remove(player);
                player.closeInventory();
                return;
            }
            SuffixData.set(player, getContent(clickedItem.getPersistentDataContainer().get(DonateManager.getNamespacedKey(), PersistentDataType.STRING)));
            player.closeInventory();
        }
    }

    private static String getContent(String id) {
        return DonateManager.donateItems.stream()
                .filter(donatItem -> donatItem.id().equals(id))
                .findFirst()
                .map(DonatItem::content)
                .orElse(null);
    }
}
