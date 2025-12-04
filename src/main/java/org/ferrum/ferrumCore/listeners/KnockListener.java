package org.ferrum.ferrumCore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.ferrum.ferrumCore.utils.FerrumListener;

public class KnockListener extends FerrumListener {

    @EventHandler
    public void onBlockKnock(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!player.getInventory().getItemInMainHand().isEmpty()) {
            return;
        }
        if (player.hasPermission("ferrum.block.knock")) {
            return;
        }

        switch (block.getType()) {
            case OAK_DOOR, OAK_TRAPDOOR, JUNGLE_DOOR, JUNGLE_TRAPDOOR, ACACIA_DOOR, ACACIA_TRAPDOOR, CHERRY_DOOR, CHERRY_TRAPDOOR, BAMBOO_DOOR, BAMBOO_TRAPDOOR -> {
                block.getWorld().playSound(block.getLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.BLOCKS, 0.8f, 0.7f);
            }

            // 🔳 Деревянные двери без дырки (сплошные)
            case SPRUCE_DOOR, SPRUCE_TRAPDOOR, BIRCH_DOOR, BIRCH_TRAPDOOR, DARK_OAK_DOOR,DARK_OAK_TRAPDOOR, MANGROVE_DOOR, MANGROVE_TRAPDOOR, PALE_OAK_DOOR, PALE_OAK_TRAPDOOR, CRIMSON_DOOR, CRIMSON_TRAPDOOR, WARPED_DOOR, WARPED_TRAPDOOR -> {
                block.getWorld().playSound(block.getLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.BLOCKS, 0.8f, 0.3f);
            }

            // 🪙 Железные двери
            case IRON_DOOR, COPPER_DOOR, EXPOSED_COPPER_DOOR, WEATHERED_COPPER_DOOR,
                 OXIDIZED_COPPER_DOOR, WAXED_COPPER_DOOR, WAXED_EXPOSED_COPPER_DOOR,
                 WAXED_WEATHERED_COPPER_DOOR, WAXED_OXIDIZED_COPPER_DOOR,
                 IRON_TRAPDOOR, COPPER_TRAPDOOR, EXPOSED_COPPER_TRAPDOOR, WEATHERED_COPPER_TRAPDOOR,
                 OXIDIZED_COPPER_TRAPDOOR, WAXED_COPPER_TRAPDOOR, WAXED_EXPOSED_COPPER_TRAPDOOR,
                 WAXED_WEATHERED_COPPER_TRAPDOOR, WAXED_OXIDIZED_COPPER_TRAPDOOR -> {
                block.getWorld().playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.BLOCKS, 0.8f, 0.0f);
            }
        }
    }
}
