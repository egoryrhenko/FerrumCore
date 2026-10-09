package org.ferrum.ferrumCore.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.ferrum.ferrumCore.FerrumCore;

import java.util.function.BooleanSupplier;

public final class Scheduler {

    private static final boolean IS_FOLIA;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        IS_FOLIA = folia;
    }

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    // -------------------
    // SYNC RUN
    // -------------------

    public static void run(Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().execute(FerrumCore.plugin, runnable);
        } else {
            Bukkit.getScheduler().runTask(FerrumCore.plugin, runnable);
        }
    }

    public static Task runRegionTimer(org.bukkit.Location location, Runnable runnable, long delay, long period) {
        if (IS_FOLIA) {
            ScheduledTask task = Bukkit.getRegionScheduler().runAtFixedRate(
                    FerrumCore.plugin,
                    location,
                    t -> runnable.run(),
                    Math.max(1, delay),
                    Math.max(1, period)
            );
            return new Task(task);
        } else {
            return new Task(Bukkit.getScheduler().runTaskTimer(FerrumCore.plugin, runnable, delay, period));
        }
    }

    public static Task runRegionLater(org.bukkit.Location location, Runnable runnable, long delay) {
        if (IS_FOLIA) {
            var task = Bukkit.getRegionScheduler().runDelayed(FerrumCore.plugin, location, t -> runnable.run(), Math.max(1, delay));
            return new Task(task);
        } else {
            return new Task(Bukkit.getScheduler().runTaskLater(FerrumCore.plugin, runnable, delay));
        }
    }


    // -------------------
    // ASYNC RUN
    // -------------------

    public static void runAsync(Runnable runnable) {
        if (IS_FOLIA) {
            Bukkit.getAsyncScheduler().runNow(FerrumCore.plugin, t -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(FerrumCore.plugin, runnable);
        }
    }

    // -------------------
    // DELAYED TASK — Runnable
    // -------------------

    public static Task runLater(Runnable runnable, long delay) {
        if (IS_FOLIA) {
            return new Task(Bukkit.getGlobalRegionScheduler()
                    .runDelayed(FerrumCore.plugin, t -> runnable.run(), delay));
        } else {
            return new Task(Bukkit.getScheduler()
                    .runTaskLater(FerrumCore.plugin, runnable, delay));
        }
    }

    // -------------------
    // DELAYED TASK — BukkitRunnable
    // -------------------

    public static Task runLater(BukkitRunnable runnable, long delay) {
        if (!IS_FOLIA) {
            return new Task(runnable.runTaskLater(FerrumCore.plugin, delay));
        }

        ScheduledTask task = Bukkit.getGlobalRegionScheduler().runDelayed(
                FerrumCore.plugin,
                t -> runnable.run(),
                delay
        );

        return new Task(task);
    }

    // -------------------
    // TIMER TASK — Runnable
    // -------------------

    public static Task runTimer(Runnable runnable, long delay, long period) {
        if (IS_FOLIA) {
            return new Task(Bukkit.getGlobalRegionScheduler()
                    .runAtFixedRate(FerrumCore.plugin, t -> runnable.run(),
                            Math.max(1, delay), period));
        } else {
            return new Task(Bukkit.getScheduler()
                    .runTaskTimer(FerrumCore.plugin, runnable, delay, period));
        }
    }

    // -------------------
    // TIMER TASK — BukkitRunnable
    // -------------------

    public static Task runTimer(BukkitRunnable runnable, long delay, long period) {
        if (!IS_FOLIA) {
            return new Task(runnable.runTaskTimer(FerrumCore.plugin, delay, period));
        }

        ScheduledTask task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                FerrumCore.plugin,
                t -> runnable.run(),
                Math.max(1, delay),
                period
        );

        return new Task(task);
    }

    public static void runWhile(BooleanSupplier condition, long delay, long period) {

        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                    FerrumCore.plugin,
                    task -> {
                        if (!condition.getAsBoolean()) {
                            task.cancel(); // ✅ Folia-safe
                        }
                    },
                    Math.max(1, delay),      // 🔥 ВАЖНО
                    Math.max(1, period)      // 🔥 тоже обязательно
            );
            return;
        }

        // Paper / Spigot
        final BukkitTask[] task = new BukkitTask[1];

        task[0] = Bukkit.getScheduler().runTaskTimer(
                FerrumCore.plugin,
                () -> {
                    if (!condition.getAsBoolean()) {
                        task[0].cancel();
                    }
                },
                delay,
                period
        );
    }

    // -------------------
    // WRAPPER
    // -------------------

    public static class Task {
        private final ScheduledTask foliaTask;
        private final BukkitTask bukkitTask;

        Task(ScheduledTask foliaTask) {
            this.foliaTask = foliaTask;
            this.bukkitTask = null;
        }

        Task(BukkitTask bukkitTask) {
            this.foliaTask = null;
            this.bukkitTask = bukkitTask;
        }

        public void cancel() {
            if (foliaTask != null) {
                foliaTask.cancel();
            } else if (bukkitTask != null) {
                bukkitTask.cancel();
            }
        }

        public boolean isCancelled() {
            if (foliaTask != null) return foliaTask.isCancelled();
            if (bukkitTask != null) return bukkitTask.isCancelled();
            return true;
        }
    }
}
