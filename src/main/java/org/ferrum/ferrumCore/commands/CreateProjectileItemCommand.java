package org.ferrum.ferrumCore.commands;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CreateProjectileItemCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player player) {
            ItemStack oldItem = player.getInventory().getItemInMainHand();

            if (oldItem.isEmpty()) {
                commandSender.sendRichMessage("<red>Возьмите предмет в руку");
                return true;
            }

            ItemStack newItem = new ItemStack(Material.SNOWBALL);
            newItem.setAmount(oldItem.getAmount());

            ItemMeta newItemMeta = newItem.getItemMeta();
            ItemMeta oldItemMeta = newItem.getItemMeta();

            if (oldItemMeta.hasDisplayName()) {
                newItemMeta.displayName(oldItemMeta.displayName());
            } else {
                newItemMeta.customName(Component.translatable(oldItem.translationKey()).decoration(TextDecoration.ITALIC, false));
            }

            newItemMeta.setItemModel(oldItem.getType().getKey());


            List<Component> lore = newItemMeta.hasLore() ? newItemMeta.lore() : new ArrayList<>();
            lore.add(Component.text("Метательный", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            newItemMeta.lore(lore);

            newItem.setItemMeta(newItemMeta);

            player.getInventory().setItem(EquipmentSlot.HAND, newItem);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }
}
