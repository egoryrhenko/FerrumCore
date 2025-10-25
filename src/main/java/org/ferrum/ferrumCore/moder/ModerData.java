package org.ferrum.ferrumCore.moder;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;


public class ModerData {
    public final Location location;
    public final ItemStack[] inventory;

    public ModerData(Location location, ItemStack[] inventory) {
        this.location = location;
        this.inventory = inventory;
    }
}
