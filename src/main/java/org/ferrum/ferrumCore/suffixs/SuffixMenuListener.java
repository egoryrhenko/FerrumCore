package org.ferrum.ferrumCore.suffixs;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.chat.util.Suffix;
import org.ferrum.ferrumCore.managers.save.SuffixData;
import org.ferrum.ferrumCore.utils.FerrumListener;

public class SuffixMenuListener extends FerrumListener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && clickedInventory.getHolder() instanceof SuffixMenu ) {

            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null) {
                return;
            }

            String id = clickedItem.getPersistentDataContainer().get(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING);

            if (id == null) {
                return;
            }

            switch (id) {
                case "__Clear__" -> {
                    SuffixData.remove(player);
                    player.closeInventory();
                }
                case "__Next__" -> {
                    SuffixMenu.setPage(player, SuffixMenu.getPage(player) + 1);
                    SuffixMenu.openSuffixMenu(player);
                }
                case "__Back__" -> {
                    SuffixMenu.setPage(player, SuffixMenu.getPage(player) - 1);
                    SuffixMenu.openSuffixMenu(player);
                }
                case "__Filter__" -> {
                    SuffixData.setFilter(player.getName(), !SuffixData.getFilter(player.getName()));
                    SuffixMenu.openSuffixMenu(player);
                    SuffixMenu.setPage(player, 0);
                    SuffixMenu.openSuffixMenu(player);
                }
                default -> {
                    Suffix clickedSuffix = getSuffix(id);

                    if (!player.hasPermission(clickedSuffix.permission())) {
                        player.sendRichMessage("<red>У вас нет прав на этот суффикс");
                        return;
                    }

                    player.sendRichMessage("<green>Выбран суффикс " + clickedSuffix.content());
                    SuffixData.set(player, clickedSuffix.content());
                    player.closeInventory();
                }
            }
        }
    }

    private static Suffix getSuffix(String id) {
        return DonateManager.donateItems.stream()
                .filter(suffix -> suffix.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}
