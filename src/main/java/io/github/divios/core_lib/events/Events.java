package io.github.divios.core_lib.events;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Events {

    public static <T extends Event> EventSubscriptionBuilder<T> suscribe(Class<T> classz) {
        return new EventSubscriptionBuilder<>(classz);
    }

    public static final class EventSubscriptionBuilder<T extends Event> {

        private final Class<T> classz;
        private final Set<Predicate<T>> filters = new HashSet<>();
        private EventPriority priority;

        private EventSubscriptionBuilder(Class<T> classz) {
            this.classz = classz;
        }

        public EventSubscriptionBuilder<T> filter(Predicate<T> filter) {
            filters.add(filter);
            return this;
        }

        public EventSubscriptionBuilder<T> setPriority(EventPriority priority) {
            this.priority = priority;
            return this;
        }

        public Subscription<T> handle(Consumer<T> consumer) {
            return consume((tSubscription, t) -> consumer.accept(t));
        }

        public Subscription<T> consume(BiConsumer<Subscription<T>, T> consumer) {
            return new SingleSubscription<T>(classz, consumer, priority, filters);
        }

    }

}
