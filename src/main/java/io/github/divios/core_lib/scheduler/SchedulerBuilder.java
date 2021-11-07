package io.github.divios.core_lib.scheduler;

import io.github.divios.core_lib.time.Ticks;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SchedulerBuilder {

    protected SchedulerBuilder() {}

    public SchedulerBuilderOptions sync() {
        return new SchedulerBuilderOptions(Schedulers.sync());
    }

    public SchedulerBuilderOptions async() {
        return new SchedulerBuilderOptions(Schedulers.async());
    }

    public static final class SchedulerBuilderOptions {

        private final Scheduler scheduler;
        private long after;
        private long every;

        private SchedulerBuilderOptions(Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        public SchedulerBuilderOptions afterAndEvery(long delay) {
            this.after = delay;
            this.every = delay;
            return this;
        }

        public SchedulerBuilderOptions afterAndEvery(long delay, TimeUnit unit) {
            this.after = Ticks.from(delay, unit);
            this.every = Ticks.from(delay, unit);
            return this;
        }

        public SchedulerBuilderOptions after(long after) {
            this.after = after;
            return this;
        }

        public SchedulerBuilderOptions after(long after, TimeUnit unit) {
            return after(Ticks.from(after, unit));
        }

        public SchedulerBuilderOptions every(long every) {
            this.every = every;
            return this;
        }

        public SchedulerBuilderOptions every(long every, TimeUnit unit) {
            return every(Ticks.from(after, unit));
        }

        public Task consume(Consumer<Task> consumer) {
            Task[] task = {null};
            if (every <= 0)
                task[0] = scheduler.runLater(() -> consumer.accept(task[0]), after);
            else
                task[0] = scheduler.runRepeating(() -> consumer.accept(task[0]), after, every);
            return task[0];
        }

        public Task run(Runnable runnable) {
            return consume(task -> runnable.run());
        }


    }

}
