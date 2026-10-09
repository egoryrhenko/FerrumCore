package org.ferrum.ferrumCore.suffixs;

import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.chat.util.Suffix;
import org.ferrum.ferrumCore.managers.ConfigManager;
import org.ferrum.ferrumCore.managers.save.SuffixData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SuffixMenu implements InventoryHolder {

    public static final NamespacedKey SUFFIX_ID = new NamespacedKey("suffix", "id");
    private final Inventory inventory;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private static final Map<UUID, Integer> selectedPage = new HashMap<>();

    public static ItemStack borderItem;
    public static ItemStack clearButton;
    public static ItemStack filterButton;
    public static ItemStack nextPageButton;
    public static ItemStack backPageButton;

    public static int startSuffixesSlot;


    public SuffixMenu(Player player) {
        inventory = Bukkit.createInventory(this,45, miniMessage.deserialize("     Меню выбора суффикса"));

        boolean filter = !SuffixData.getFilter(player.getName());
        List<Suffix> showSuffix = DonateManager.donateItems.stream().filter(suffix -> player.hasPermission(suffix.permission()) || filter).toList();

        buildMenu(player, inventory, ConfigManager.getMenuShape(), showSuffix);
    }


    public static void openSuffixMenu(Player player) {
        player.openInventory(new SuffixMenu(player).getInventory());
    }

    private void buildMenu(Player player, Inventory inventory, List<String> shape, List<Suffix> suffixes) {
        int countSuffixes = suffixes.size();
        int page = getPage(player);

        List<ItemStack> suffixItems = new ArrayList<>();

        Suffix selectedSuffix = suffixes.stream().filter(s -> s.content().equals(SuffixData.get(player))).findFirst().orElse(null);

        for (Suffix suffix : suffixes) {
            ItemStack suffixItem = new ItemStack(Material.STONE);

            ItemMeta suffixItemMeta = suffixItem.getItemMeta();
            suffixItemMeta.setItemModel(suffix.material().getKey());
            suffixItemMeta.displayName(miniMessage.deserialize("<white>" + suffix.name()).decoration(TextDecoration.ITALIC, false));
            suffixItemMeta.getPersistentDataContainer().set(SUFFIX_ID, PersistentDataType.STRING, suffix.id());
            suffixItemMeta.lore(List.of(miniMessage.deserialize("<gray>" + ((selectedSuffix != null && selectedSuffix.equals(suffix)) ? "Уже выбран" : "Нажмите чтобы выбрать")).decoration(TextDecoration.ITALIC, false)));
            suffixItem.setItemMeta(suffixItemMeta);
            suffixItems.add(suffixItem);
        }


        int id = 21 * page;

        for (int row = 0; row < shape.size(); row++) {
            String line = shape.get(row);
            for (int col = 0; col < 9; col++) {
                char c = line.charAt(col);
                int slot = row * 9 + col;
                switch (c) {
                    case '#' -> inventory.setItem(slot, borderItem);
                    case 'C' -> inventory.setItem(slot, clearButton);
                    case 'F' -> {
                        ItemMeta filterButtonMeta = filterButton.getItemMeta();
                        filterButtonMeta.lore(List.of(miniMessage.deserialize("<gray>" + (SuffixData.getFilter(player.getName()) ? "Доступные" : "Все")).decoration(TextDecoration.ITALIC, false)));
                        filterButton.setItemMeta(filterButtonMeta);
                        inventory.setItem(slot, filterButton);
                    }
                    case 'N' -> inventory.setItem(slot, (countSuffixes - 21 * page) > 21 ? nextPageButton : borderItem);
                    case 'B' -> inventory.setItem(slot, page > 0 ? backPageButton : borderItem);
                    default -> {
                        if (slot < startSuffixesSlot) {
                            continue;
                        }
                        if (!(id < suffixItems.toArray().length)) continue;
                        inventory.setItem(slot, suffixItems.get(id));
                        id++;
                    }
                }
            }
        }

    }

    public static void setPage(Player player, int page) {
        selectedPage.put(player.getUniqueId(), page);
    }

    public static int getPage(Player player) {
        return selectedPage.getOrDefault(player.getUniqueId(), 0);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
