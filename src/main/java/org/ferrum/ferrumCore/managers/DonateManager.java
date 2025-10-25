package org.ferrum.ferrumCore.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.chat.util.ChatUtil;
import org.ferrum.ferrumCore.chat.util.DonatItem;
import org.ferrum.ferrumCore.managers.save.SuffixData;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;

public class DonateManager implements Listener, CommandExecutor {

    private final LuckPerms luckPerms;
    public static List<DonatItem> donateItems = new ArrayList<>();

    private final Component title = Component.text("Меню выбора суффикса");
    private final NamespacedKey key = new NamespacedKey("ferrum", "donat_item_id");

    private final ItemStack removeButton;
    private final ItemStack borderItem;


    public DonateManager(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;

        borderItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta itemMeta = borderItem.getItemMeta();
        itemMeta.displayName(Component.empty());
        borderItem.setItemMeta(itemMeta);

        removeButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        itemMeta.displayName(ChatUtil.formatText("Убрать суфикс").decoration(TextDecoration.ITALIC, false));
        removeButton.setItemMeta(itemMeta);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && event.getView().title().equals(title)) {
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
            SuffixData.set(player, getContent(clickedItem.getPersistentDataContainer().get(key, PersistentDataType.STRING)));
            player.closeInventory();
        }
    }

    public void openDonateMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player, 36, title);

        for (int slot = 0; slot<36; slot++) {
            if (slot < 10|| slot == 17 || slot == 18 || slot > 25) {
                menu.setItem(slot, borderItem);
            }
        }

        menu.setItem(31,removeButton);

        int i = 10;
        for (DonatItem donatItem : donateItems){
            if (player.hasPermission(donatItem.getPermission())){
                if (Objects.equals(menu.getItem(i), borderItem)){
                    i++;
                    continue;
                }

                ItemStack item = new ItemStack(donatItem.getMaterial());
                ItemMeta meta = item.getItemMeta();
                meta.displayName(ChatUtil.formatText(donatItem.getName()).decoration(TextDecoration.ITALIC, false));
                meta.lore(List.of(ChatUtil.formatText("&7Осталось " +checkPermissionTime(player,donatItem.getPermission())).decoration(TextDecoration.ITALIC, false)));

                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, donatItem.getId());

                item.setItemMeta(meta);
                menu.setItem(i, item);
                i++;
            }
        }

        player.openInventory(menu);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (sender instanceof Player player){
            openDonateMenu(player);
        } else {
            sender.sendMessage("Окак");
        }
        return true;
    }

    public String checkPermissionTime(Player player, String permission) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return "Error 418";
        }
        for (Node node : user.getNodes(NodeType.PERMISSION)) {

            if (!node.getKey().equals(permission)) {
                continue;
            }

            if (!node.hasExpiry()) {
                return "&5∞ &fТиков";
            }

            int timeSeconds = (int) (node.getExpiry().getEpochSecond() - Instant.now().getEpochSecond());

            return getFormatTime(timeSeconds);
        }
        return "&5∞ &fТиков";
    }

    private static String getContent(String id) {
        return donateItems.stream()
                .filter(donatItem -> donatItem.getId().equals(id))
                .findFirst()
                .map(DonatItem::getContent)
                .orElse(null);
    }

    private static String getContentByPerm(String permission) {
        return donateItems.stream()
                .filter(donatItem -> donatItem.getPermission().equals(permission))
                .findFirst()
                .map(DonatItem::getContent)
                .orElse(null);
    }

    private static String getPerm(String id) {
        return donateItems.stream()
                .filter(donatItem -> donatItem.getId().equals(id))
                .findFirst()
                .map(DonatItem::getPermission)
                .orElse(null);
    }


    public static String getFormatTime(int time) {
        if (0 > time) return "Error";
        int days = time / 86400;
        int hours = (time % 86400) / 3600;
        int minutes = (time % 3600) / 60;

        StringBuilder result = new StringBuilder();

        int i = 0;

        if (days > 0) {
            result.append(days).append(days == 1 ? " день " : " дней ");
            i++;
        }
        if (hours > 0) {
            result.append(hours).append(hours == 1 ? " час " : " часов ");
            i++;
        }
        if (minutes > 0 && i < 2) {
            result.append(minutes).append(minutes == 1 ? " минута " : " минут ");
        }

        if (result.isEmpty()) return "меньше тика";

        return result.toString().trim();
    }

    public static void handle(NodeMutateEvent event, Node node, boolean add) {
        if (event.isUser() && node.getType() == NodeType.PERMISSION) {
            PermissionNode permNode = NodeType.PERMISSION.cast(node);
            String permission = permNode.getPermission();

            FerrumCore.error(permission);

            if (!permission.startsWith("suffix")) {
                return;
            }

            UUID uuid = ((User) event.getTarget()).getUniqueId();
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            String con = getContentByPerm(permission);
            if (add) {
                FerrumCore.log(con);
                if (con == null) {
                    return;
                }
                if (SuffixData.has(player)) {
                    return;
                }
                SuffixData.set(player, con);

            } else {
                if (con == null) {
                    return;
                }
                if (!SuffixData.has(player)) {
                    return;
                }
                if (SuffixData.get(player).equals(con)) {
                    SuffixData.remove(player);
                }
            }
        }
    }
}


