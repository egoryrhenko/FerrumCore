package org.ferrum.ferrumCore.suffixs.roll;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.chat.util.Suffix;
import org.ferrum.ferrumCore.managers.save.SuffixData;
import org.ferrum.ferrumCore.suffixs.DonateManager;
import org.ferrum.ferrumCore.suffixs.SuffixMenu;
import org.ferrum.ferrumCore.utils.FerrumCommand;
import org.ferrum.ferrumCore.utils.FerrumListener;
import org.ferrum.ferrumCore.utils.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CaseMenu implements InventoryHolder {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private final List<Suffix> allSuffix;

    private final static Map<UUID, CaseMenu> menus = new HashMap<>();

    private final Inventory inventory;
    private final Random random;
    private Scheduler.Task task;
    public int animationTicks = 160;

    public void stop() {
        if (task != null) {
            task.cancel();
        }
        task = null;
    }


    public static void createSuffixMenu(Player player) {
        menus.put(player.getUniqueId(), new CaseMenu(player));
        openSuffixMenu(player);
    }

     public static void openSuffixMenu(Player player) {
        player.openInventory(menus.get(player.getUniqueId()).getInventory());
     }

    public CaseMenu(Player player) {
        random = new Random();
        this.inventory = Bukkit.createInventory(this, 27);
        this.allSuffix = DonateManager.donateItems;
        start(player);
    }


    public void update(List<Suffix> allSuffix) {
        for (int i = 16; i > 8; i--) {
            inventory.setItem(i + 1, inventory.getItem(i));
        }

        Suffix suffix =  allSuffix.get(random.nextInt(allSuffix.size()));

        ItemStack suffixItem = new ItemStack(Material.STONE);

        ItemMeta suffixItemMeta = suffixItem.getItemMeta();
        suffixItemMeta.setItemModel(suffix.material().getKey());
        suffixItemMeta.displayName(miniMessage.deserialize("<white>" + suffix.name()).decoration(TextDecoration.ITALIC, false));
        suffixItemMeta.getPersistentDataContainer().set(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING, suffix.id());
        suffixItem.setItemMeta(suffixItemMeta);
        inventory.setItem(9, suffixItem);
    }


    public void forceFinish(Player player) {
        menus.remove(player.getUniqueId());
        ItemStack suffixItem = inventory.getItem(random.nextInt(9,13));

        if (suffixItem == null) {
            return;
        }

        PersistentDataContainerView dataContainer = suffixItem.getPersistentDataContainer();

        if (!(dataContainer.has(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING))) {
            return;
        }

        String suffixId = dataContainer.get(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING);

        Suffix suffix = DonateManager.getSuffixById(suffixId);
        player.sendRichMessage("<green>Поздравляем вы выйграли " + suffix.name());
        FerrumCore.runCommand("/lp user " + player.getName() + " permission set " + suffix.permission());
    }

    private void finish(Player player) {
        menus.remove(player.getUniqueId());
        ItemStack suffixItem = inventory.getItem(13);

        if (suffixItem == null) {
            return;
        }

        PersistentDataContainerView dataContainer = suffixItem.getPersistentDataContainer();

        if (!(dataContainer.has(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING))) {
            return;
        }

        String suffixId = dataContainer.get(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING);

        Suffix suffix = DonateManager.getSuffixById(suffixId);
        player.sendRichMessage("<green>Поздравляем вы выйграли " + suffix.name());
        FerrumCore.runCommand("say 1");
        FerrumCore.runCommandNotSafe("say 2");
        FerrumCore.runCommand("/lp user " + player.getName() + " permission set " + suffix.permission());
        Scheduler.runLater(player::closeInventory, 20);
    }


    private void start(Player player) {
        task = Scheduler.runTimer(new Runnable() {
            int i = 1;
            final Sound sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
            @Override
            public void run() {
                if (animationTicks < 1) {
                    task.cancel();
                    finish(player);
                    return;
                }
                if (animationTicks % i == 0) {
                    update(allSuffix);
                    player.playSound(player, sound, 0.8f, 1.2f);
                }
                if (animationTicks % 40 == 0) {
                    i++;
                }


                animationTicks--;
            }
        }, 0L, 1L);
    }


    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
