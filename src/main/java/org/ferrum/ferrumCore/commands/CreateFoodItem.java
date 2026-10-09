package org.ferrum.ferrumCore.commands;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.model.data.DataType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.ferrum.ferrumCore.utils.FerrumCommand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CreateFoodItem extends FerrumCommand {

    public CreateFoodItem() {
        super("add-food-properties",true);
        setDescription("Делает предмет съедобным");
        setAliases(List.of("afp", "fp","edible"));
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] args) {

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Команда только для игроков");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendRichMessage("<red>Возьмите предмет в руку");
            return true;
        }

        if (item.hasData(DataComponentTypes.CONSUMABLE)) {
            player.sendRichMessage("<red>Предмет у вас в руке уже съедобен");
            return true;
        }

        item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable().build());
        item.setData(
                DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(0)
                .saturation(0f)
                .canAlwaysEat(true)
        );
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
        lore.add(Component.text("Съедобный", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        item.setItemMeta(meta);
        player.getInventory().setItem(EquipmentSlot.HAND, item);

        player.sendRichMessage("<green>Предмет в руке стал съедобным");
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        return List.of();
    }

}
