package io.github.divios.core_lib.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Events {

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public static <T extends Event> EventSubscriptionBuilder<T> subscribe(Class<T> classz) {
        return new EventSubscriptionBuilder<>(classz);
    }

    public static <T extends Event> EventSubscriptionBuilder<T> subscribe(Class<T> classz, EventPriority priority) {
        return new EventSubscriptionBuilder<>(classz);
    }

    public static final class EventSubscriptionBuilder<T extends Event> {

        private final Class<T> classz;
        private final Set<Predicate<T>> filters = new HashSet<>();
        private EventPriority priority;

        private EventSubscriptionBuilder(Class<T> classz) {
            this(classz, EventPriority.NORMAL);
        }

        private EventSubscriptionBuilder(Class<T> classz, EventPriority priority) {
            this.classz = classz;
            this.priority = priority;
        }

        public EventSubscriptionBuilder<T> filter(Predicate<T> filter) {
            filters.add(filter);
            return this;
        }

        public EventSubscriptionBuilder<T> setPriority(EventPriority priority) {
            this.priority = priority;
            return this;
        }

        public Subscription handler(Consumer<T> consumer) {
            return biHandler((tSubscription, t) -> consumer.accept(t));
        }

        public Subscription biHandler(BiConsumer<Subscription, T> consumer) {
            return new SingleSubscription(classz, consumer, priority, filters);
        }

    }

}
