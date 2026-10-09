package org.ferrum.ferrumCore.pricol.anime;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.ferrum.ferrumCore.utils.Scheduler;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;

public final class BreakManager {

    // Карта тик -> список блоков, которые нужно восстановить
    private static final Map<Long, List<BlockEntry>> schedule = new ConcurrentHashMap<>();

    // Глобальный тикер
    private static Scheduler.Task tickerTask = null;
    private static long currentTick = 0L;

    private BreakManager() {}

    public static void startTicker() {
        if (tickerTask != null) return;
        tickerTask = Scheduler.runTimer(BreakManager::tick, 1, 1);
    }

    public static void stopTicker() {
        if (tickerTask != null) {
            tickerTask.cancel();
            tickerTask = null;
        }
    }

    public static void logBlock(Block block, long delayTicks) {
        long targetTick = currentTick + delayTicks;
        BlockEntry entry = BlockEntry.capture(block);
        schedule.compute(targetTick, (t, list) -> {
            if (list == null) list = new ArrayList<>();
            list.add(entry);
            return list;
        });
        startTicker();
    }

    private static void tick() {
        currentTick++;
        List<BlockEntry> toRestore = schedule.remove(currentTick);
        if (toRestore == null) return;

        for (BlockEntry entry : toRestore) {
            restore(entry);
        }

        // Выключаем тикер, если задач больше нет
        if (schedule.isEmpty()) stopTicker();
    }

    private static void restore(BlockEntry e) {
        Block block = e.pos.world.getBlockAt(e.pos.x, e.pos.y, e.pos.z);
        block.setBlockData(e.data, false);

        if (e.contents != null && block.getState() instanceof InventoryHolder h) {
            h.getInventory().setContents(e.contents);
        }
    }

    // === Вспомогательные record-и ===

    record BlockPos(int x, int y, int z, World world) {

        static BlockPos of(Block b) {
            Location loc = b.getLocation();
            return new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld());
        }

        Location toLocation() {
            return new Location(world, x, y, z);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z, world == null ? null : world.getUID());
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BlockPos other)) return false;
            if (this.x != other.x || this.y != other.y || this.z != other.z) return false;
            if (this.world == null || other.world == null) return this.world == other.world;
            return this.world.getUID().equals(other.world.getUID());
        }
    }

    record BlockEntry(BlockPos pos, BlockData data, ItemStack[] contents) {

        static BlockEntry capture(Block block) {
            BlockPos pos = BlockPos.of(block);
            BlockData data = block.getBlockData().clone();

            ItemStack[] contents = null;
            if (block.getState() instanceof InventoryHolder h) {
                contents = Arrays.copyOf(h.getInventory().getContents(), h.getInventory().getSize());
            }

            return new BlockEntry(pos, data, contents);
        }
    }
}
