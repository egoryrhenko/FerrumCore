package org.ferrum.ferrumCore.pricol.anime;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.ferrum.ferrumCore.FerrumCore;

import java.util.*;

public class BreakManager {

    private static final Map<BlockPos, SavedBlock> memory = new HashMap<>();
    private static BukkitTask task;

    public static void logBlock(Block block, int ticks) {
        BlockPos pos = BlockPos.of(block);
        if (memory.containsKey(pos)) return; // уже есть в памяти

        BlockData data = block.getBlockData();
        ItemStack[] contents = null;

        if (block.getState() instanceof InventoryHolder holder) {
            contents = Arrays.copyOf(holder.getInventory().getContents(), holder.getInventory().getSize());
        }

        int restoreTime = Bukkit.getCurrentTick() + ticks;
        memory.put(pos, new SavedBlock(data, contents, restoreTime));

        // если таск не работает — запускаем
        if (task == null) {
            startTask();
        }
    }

    private static void startTask() {
        task = Bukkit.getScheduler().runTaskTimer(FerrumCore.plugin, BreakManager::tick, 1L, 1L);
    }

    private static void stopTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private static void tick() {
        long now = Bukkit.getCurrentTick();
        Iterator<Map.Entry<BlockPos, SavedBlock>> it = memory.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<BlockPos, SavedBlock> entry = it.next();
            SavedBlock saved = entry.getValue();

            if (saved.restoreTime <= now) {
                restoreBlock(entry.getKey(), saved);
                it.remove();
            }
            if (memory.isEmpty()) {
                stopTask();
            }
        }
    }

    private static void restoreBlock(BlockPos pos, SavedBlock saved) {
        if (pos.world == null) return;

        Block block = pos.world.getBlockAt(pos.x, pos.y, pos.z);
        block.setBlockData(saved.data, false);

        if (saved.contents != null && block.getState() instanceof InventoryHolder holder) {
            holder.getInventory().setContents(saved.contents);
        }
    }

    private record SavedBlock(BlockData data, ItemStack[] contents, long restoreTime) {}

    private record BlockPos(int x, int y, int z, World world) {
        static BlockPos of(Block b) {
            Location loc = b.getLocation();
            return new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld());
        }
    }

    public static void clear() {
        stopTask();
        memory.clear();
    }
}
