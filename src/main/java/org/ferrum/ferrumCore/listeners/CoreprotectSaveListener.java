package org.ferrum.ferrumCore.listeners;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.FerrumListener;

import java.io.Console;
import java.util.Iterator;

public class CoreprotectSaveListener implements Listener {
    @EventHandler
    public void onWindChargeExplode(EntityExplodeEvent event) {
        if (event.getEntity().getType() != EntityType.WIND_CHARGE) {
            return;
        }

        // Удаляем обычные блоки из списка разрушения
        Iterator<Block> iterator = event.blockList().iterator();

        while (iterator.hasNext()) {
            Material type = iterator.next().getType();

            if (!canWindChargeInteract(type)) {
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onMaceExplode(BlockExplodeEvent event) {
        System.out.println(event.getExplodedBlockState().getBlock().getType().toString());
        System.out.println(event.getBlock().getType().toString());
        if (!event.getBlock().getType().isAir()) {
            return;
        }

        // Удаляем обычные блоки из списка разрушения
        Iterator<Block> iterator = event.blockList().iterator();

        while (iterator.hasNext()) {
            Material type = iterator.next().getType();

            if (!canWindChargeInteract(type)) {
                iterator.remove();
            }
        }
    }

    public boolean canWindChargeInteract(Material material) {
        return Tag.DOORS.isTagged(material)
                || Tag.WOODEN_DOORS.isTagged(material)
                || Tag.WOODEN_TRAPDOORS.isTagged(material)
                || Tag.FENCE_GATES.isTagged(material)
                || Tag.BUTTONS.isTagged(material)
                || Tag.PRESSURE_PLATES.isTagged(material)
                || material == Material.LEVER;
    }
}
