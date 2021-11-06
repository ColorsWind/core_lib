package io.github.divios.core_lib.scheduler;

import io.github.divios.core_lib.Core_lib;
import io.github.divios.core_lib.utils.TriConsumer;
import org.bukkit.Bukkit;

import java.util.function.BiConsumer;

public enum ThreadStrategy {

    async((R, I) -> Bukkit.getScheduler().runTaskLaterAsynchronously(Core_lib.PLUGIN, R, I)),
    sync((R, I) -> Bukkit.getScheduler().runTaskLater(Core_lib.PLUGIN, R, I));

    private final BiConsumer<Runnable, Integer> action;

    ThreadStrategy(BiConsumer<Runnable, Integer> action)  {
        this.action = action;
    }

    protected void run(Runnable runnable, Integer delay) {
        action.accept(runnable, delay);
    }

}
