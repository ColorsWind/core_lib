package io.github.divios.core_lib.misc;

import io.github.divios.core_lib.Core_lib;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A compact way to define a Listener using a lambda
 * @author Redempt
 *
 * @param <T> The event being listened for
 */
@Deprecated
public class EventListener<T extends Event> implements Listener {

    private Plugin plugin = Core_lib.PLUGIN;
    private BiConsumer<EventListener<T>, T> handler;
    private Class<T> eventClass;

    /**
     * Creates and registers a Listener for the given event
     * @param eventClass The class of the event being listened for
     * @param priority The EventPriority for this listener
     * @param handler The callback to receive the event and this EventListener
     */
    public EventListener(Class<T> eventClass, EventPriority priority, BiConsumer<EventListener<T>, T> handler) {
        this.handler = handler;
        this.eventClass = eventClass;
        Bukkit.getPluginManager().registerEvent(eventClass, this, priority, (l, e) -> handleEvent((T) e), plugin);
    }

    /**
     * Creates and registers a Listener for the given event
     * @param eventClass The class of the event being listened for
     * @param priority The EventPriority for this listener
     * @param handler The callback to receive the event
     */
    public EventListener(Class<T> eventClass, EventPriority priority, Consumer<T> handler) {
        this(eventClass, priority, (l, e) -> handler.accept(e));
    }

    /**
     * Creates and registers a Listener for the given event
     * @param eventClass The class of the event being listened for
     * @param handler The callback to receive the event and this EventListener
     */
    public EventListener(Class<T> eventClass, BiConsumer<EventListener<T>, T> handler) {
        this(eventClass, EventPriority.NORMAL, handler);
    }

    /**
     * Creates and registers a Listener for the given event
     * @param eventClass The class of the event being listened for
     * @param handler The callback to receive the event
     */
    public EventListener(Class<T> eventClass, Consumer<T> handler) {
        this(eventClass, EventPriority.NORMAL, handler);
    }

    @EventHandler
    public void handleEvent(T event) {
        if (event.getClass().equals(eventClass)) {
            handler.accept(this, event);
        }
    }

    /**
     * Unregisters this listener
     */
    public void unregister() {
        HandlerList.unregisterAll(this);
    }


    public static class Builder<T extends Event> {

        private Plugin plugin = null;
        private BiConsumer<EventListener<T>, T> handler = null;
        private Class<T> eventClass = null;
        private EventPriority priority = null;

        public Builder(Plugin plugin) {
            this.plugin = plugin;
        }

        public Builder<T> suscribe(Class<T> eventClass, EventPriority priority) {
            this.eventClass = eventClass;
            this.priority = priority;
            return this;
        }

        public Builder<T> suscribe(Class<T> eventClass) {
            return suscribe(eventClass, EventPriority.NORMAL);
        }

        public EventListener<T> handler(BiConsumer<EventListener<T>, T> handler) {
            this.handler = handler;

            Objects.requireNonNull(eventClass, "Event class cannot be null");
            Objects.requireNonNull(handler, "Handle cannot be null");

            if (priority == null) priority = EventPriority.NORMAL;

            return new EventListener<>(eventClass, priority, handler);
        }

    }

}