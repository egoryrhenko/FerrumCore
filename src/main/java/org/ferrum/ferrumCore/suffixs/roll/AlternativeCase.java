package org.ferrum.ferrumCore.suffixs.roll;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.apache.http.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.chat.util.ChatUtil;
import org.ferrum.ferrumCore.chat.util.Suffix;
import org.ferrum.ferrumCore.suffixs.DonateManager;
import org.ferrum.ferrumCore.suffixs.SuffixMenu;
import org.ferrum.ferrumCore.utils.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AlternativeCase {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private List<Suffix> allSuffix;


    private final String[] content = new String[]{" ", " ", " ", " ", " ", " ", " ", " "," "};
    private final Random random;
    private Scheduler.Task task;
    public int animationTicks = 200;
    public boolean open = true;

    public void stop() {
        if (task != null) {
            task.cancel();
        }
        task = null;
    }




    public AlternativeCase(Player player) {
        random = new Random();
        this.allSuffix = DonateManager.donateItems;
        start(player);
    }


    public void update(List<Suffix> allSuffix) {
        for (int i = 7; i > -1; i--) {
            content[i + 1] = content[i];
        }

        Suffix suffix =  allSuffix.get(random.nextInt(allSuffix.size()));

        ItemStack suffixItem = new ItemStack(Material.STONE);

        ItemMeta suffixItemMeta = suffixItem.getItemMeta();
        suffixItemMeta.setItemModel(suffix.material().getKey());
        suffixItemMeta.displayName(miniMessage.deserialize("<white>" + suffix.name()).decoration(TextDecoration.ITALIC, false));
        suffixItemMeta.getPersistentDataContainer().set(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING, suffix.id());
        suffixItem.setItemMeta(suffixItemMeta);


        content[0] = suffix.content();
    }


    private void finish(Player player) {
        String suffixItem = content[4];

        if (suffixItem == null) {
            return;
        }

        //PersistentDataContainerView dataContainer = suffixItem.getPersistentDataContainer();

        //if (!(dataContainer.has(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING))) {
        //    return;
        //}

        //String suffixId = dataContainer.get(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING);

        //Suffix suffix = DonateManager.getSuffixById(suffixId);
        player.sendRichMessage("<lime>Поздравляем вы выйграли " + suffixItem);
        FerrumCore.runCommand("/lp user " + player.getName() + " permission set " + suffixItem);
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
                    showMyTitle(player);
                    player.playSound(player, sound, 0.8f, 1.2f);
                }
                if (animationTicks % 50 == 0) {
                    i++;
                }


                animationTicks--;
            }
        }, 0L, 1L);
    }



    private void showMyTitle(Player player) {
        final Component mainTitle = ChatUtil.formatText(String.join("", content));
        final Component subtitle = Component.text("⏶", NamedTextColor.GRAY);
        final Title.Times times = Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(3000), Duration.ofMillis(0));

        //player.sendMessage(mainTitle);

        final Title title = Title.title(miniMessage.deserialize(miniMessage.serialize(mainTitle)), subtitle, times);
        player.showTitle(title);
    }
}
