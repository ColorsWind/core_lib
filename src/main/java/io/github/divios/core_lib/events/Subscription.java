package io.github.divios.core_lib.events;

import org.bukkit.event.Event;

public interface Subscription<T extends Event> {

    void unregister();

}
