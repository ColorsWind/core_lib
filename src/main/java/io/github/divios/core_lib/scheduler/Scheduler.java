package io.github.divios.core_lib.scheduler;

import io.github.divios.core_lib.time.Ticks;

import java.util.concurrent.TimeUnit;

public interface Scheduler {

    default Task run(Runnable runnable) {
        return runLater(runnable, 0);
    }

    Task runLater(Runnable runnable, long delayTicks);

    default Task runLater(Runnable runnable, long delayTicks, TimeUnit units) {
        return runLater(runnable, Ticks.from(delayTicks, units));
    }

    Task runRepeating(Runnable runnable, long delayTicks, long repeatTicks);

    default Task runRepeating(Runnable runnable, long delayTicks, TimeUnit delayUnit, long repeatTicks, TimeUnit repeatUnit) {
        return runRepeating(runnable, Ticks.from(delayTicks, delayUnit), Ticks.from(repeatTicks, repeatUnit));
    }

}
