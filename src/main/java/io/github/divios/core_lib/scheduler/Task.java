package io.github.divios.core_lib.scheduler;

public interface Task {

    boolean isDone();

    boolean isRunning();

    void stop();

    int getId();

    default void close() {
        stop();
    }

}
