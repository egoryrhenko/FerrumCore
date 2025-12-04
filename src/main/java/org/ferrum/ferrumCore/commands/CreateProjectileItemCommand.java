package org.ferrum.ferrumCore.commands;

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

import java.util.List;

public class CreateProjectileItemCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.isEmpty()) {
                commandSender.sendMessage("wozmi item in hand");
                return true;
            }

            ItemStack newItem = new ItemStack(Material.SNOWBALL);

            ItemMeta newItemMeta = newItem.getItemMeta();
            ItemMeta oldItemMeta = newItem.getItemMeta();

            newItemMeta.setItemModel(oldItemMeta.getItemModel());
            newItemMeta.customName(oldItemMeta.displayName());

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
