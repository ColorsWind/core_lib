package io.github.divios.core_lib.scheduler;

import io.github.divios.core_lib.Core_lib;
import io.github.divios.core_lib.utils.TriConsumer;
import org.bukkit.Bukkit;

public enum ThreadStrategy {

    async((R, I, P) -> Bukkit.getScheduler().runTaskLaterAsynchronously(Core_lib.PLUGIN, R, I)),
    sync((R, I, P) -> Bukkit.getScheduler().runTaskLater(Core_lib.PLUGIN, R, I)),

    private final BiConsumer<Runnable, Integer, Integer> action;

    ModeStrategy(TriConsumer<Runnable, Integer, Integer> action)  {
        this.action = action;
    }

    protected void run(Runnable runnable, Integer delay, Integer period) {
        action.accept(runnable, delay, period);
    }

}
