package io.github.divios.core_lib.scheduler;

import io.github.divios.core_lib.Core_lib;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class Schedulers {

    public static Scheduler sync() {
        return new syncScheduler();
    }

    public static Scheduler async() {
        return new asyncScheduler();
    }

    public static BukkitScheduler bukkitScheduler() {
        return Bukkit.getScheduler();
    }

    public static final class syncScheduler implements Scheduler {

        @Override
        public Task runLater(Runnable runnable, long delayTicks) {
            int id = bukkitScheduler().runTaskLater(Core_lib.PLUGIN, runnable, delayTicks).getTaskId();
            return new SchedulerTask(id);
        }

        @Override
        public Task runRepeating(Runnable runnable, long delayTicks, long repeatTicks) {
            int id = bukkitScheduler().scheduleSyncRepeatingTask(Core_lib.PLUGIN, runnable, delayTicks, repeatTicks);
            return new SchedulerTask(id);
        }

    }

    public static final class asyncScheduler implements Scheduler {

        @Override
        public Task runLater(Runnable runnable, long delayTicks) {
            int id = bukkitScheduler().runTaskLaterAsynchronously(Core_lib.PLUGIN, runnable, delayTicks).getTaskId();
            return new SchedulerTask(id);
        }

        @Override
        public Task runRepeating(Runnable runnable, long delayTicks, long repeatTicks) {
            int id = bukkitScheduler().scheduleAsyncRepeatingTask(Core_lib.PLUGIN, runnable, delayTicks, repeatTicks);
            return new SchedulerTask(id);
        }

    }

    public static final class SchedulerTask implements Task {

        private final int bukkitId;

        private SchedulerTask(int bukkitId) {
            this.bukkitId = bukkitId;
        }

        @Override
        public boolean isDone() {
            return !(isRunning() || bukkitScheduler().isQueued(bukkitId));
        }

        @Override
        public boolean isRunning() {
            return bukkitScheduler().isCurrentlyRunning(bukkitId);
        }

        @Override
        public void stop() {
            bukkitScheduler().cancelTask(bukkitId);
        }

        @Override
        public int getId() {
            return bukkitId;
        }
    }

}
