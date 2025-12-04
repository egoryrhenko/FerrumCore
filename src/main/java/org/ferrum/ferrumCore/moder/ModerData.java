package org.ferrum.ferrumCore.moder;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;


public record ModerData(Location location, ItemStack[] inventory) {
}
