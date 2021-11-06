package io.github.divios.core_lib.scheduler;

public class Scheduler {



    public static final class ScheculerDone {

        private final ModeStrategy strategy;

        public ScheculerDone(ModeStrategy strategy) {
            this.strategy = strategy;
        }

        public void run(Runnable r) {
            strategy.run(r, 0, 0);
        }

        public void runLater(Runnable r, int delay) {
            strategy.run(r, delay, 0);
        }

    }

}
