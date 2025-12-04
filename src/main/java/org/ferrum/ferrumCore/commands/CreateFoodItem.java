package org.ferrum.ferrumCore.commands;

import io.papermc.paper.datacomponent.item.FoodProperties;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.ferrum.ferrumCore.utils.FerrumCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreateFoodItem extends FerrumCommand {

    public CreateFoodItem() {
        super("create-food-item");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] args) {

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Команда только для игроков");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("Использование: /create-food-item <nutrition> <saturation>");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage("Возьмите предмет в руку");
            return true;
        }

        int nutrition;
        float saturation;

        try {
            nutrition = Integer.parseInt(args[0]);
            saturation = Float.parseFloat(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Числа введены неправильно!");
            return true;
        }

        ItemMeta im = item.getItemMeta();
        im.setUseRemainder(null);
        UseCooldownComponent cooldown = im.getUseCooldown();
        cooldown.setCooldownSeconds(1.0f);
        im.setUseCooldown(cooldown);
        FoodComponent foodComponent = im.getFood();
        foodComponent.setCanAlwaysEat(true);
        foodComponent.setNutrition(4);
        foodComponent.setSaturation(1.0f);
        im.setFood(foodComponent);
        im.setDisplayName(ChatColor.WHITE + "Elixir of Life");
        im.setLore(List.of("Use to trigger a", "Time Lord regeneration"));
        item.setItemMeta(im);

        player.getInventory().setItem(EquipmentSlot.HAND, item);

        player.sendMessage("✔ Предмет стал съедобным! (+" + nutrition + " еды, насыщение " + saturation + ")");
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        return List.of();
    }

}
