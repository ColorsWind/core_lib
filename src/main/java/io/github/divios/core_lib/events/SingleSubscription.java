package io.github.divios.core_lib.events;

import io.github.divios.core_lib.Core_lib;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class SingleSubscription<T extends Event> implements Listener, Subscription {

    private final Class<T> classz;
    private final BiConsumer<Subscription, T> action;
    private final EventPriority priority;
    private final Set<Predicate<T>> filters = new HashSet<>();

    protected SingleSubscription(Class<T> classz, BiConsumer<Subscription, T> action, EventPriority priority, Set<Predicate<T>> filters) {
        this.classz = classz;
        this.action = action;
        this.priority = priority;
        this.filters.addAll(filters);

        initialize();
    }

    private void initialize() {
        Bukkit.getPluginManager().registerEvent(classz, this, priority, (listener, event) -> handleEvent((T) event), Core_lib.PLUGIN);
    }

    private void handleEvent(T e) {
        if (e.getClass().equals(classz) && passFilters(e)) {
            action.accept(this, e);
        }
    }

    private boolean passFilters(T e) {
        for (Predicate<T> filter : filters) {
            if (!filter.test(e)) return false;
        }
        return true;
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
