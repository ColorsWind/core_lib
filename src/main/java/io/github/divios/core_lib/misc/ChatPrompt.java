package io.github.divios.core_lib.misc;

import com.cryptomorin.xseries.messages.Titles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ChatPrompt {

    private final Plugin plugin;
    private final Player p;
    private final BiConsumer<Player, String> onComplete;
    private final Consumer<Player> onExpired;
    private final Task TaskID;

    private final EventListener<AsyncPlayerChatEvent> listener;

    public ChatPrompt(Plugin plugin, Player p,
                      BiConsumer<Player, String> onComplete,
                      Consumer<Player> onExpired,
                      String title,
                      String subtitle
    ) {
        this.plugin = plugin;
        this.p = p;
        this.onComplete = onComplete;
        this.onExpired = onExpired;

        listener = new EventListener<>(plugin, AsyncPlayerChatEvent.class,
                EventPriority.HIGHEST, this::chatListener);

        TaskID = Task.syncDelayed(plugin, () -> {
            listener.unregister();
            this.onExpired.accept(p);
        }, 400);

        p.closeInventory();
        Titles.sendTitle(p, 10, 25, 10,
                FormatUtils.color(title), FormatUtils.color(subtitle));

    }


    public void chatListener(EventListener<AsyncPlayerChatEvent> lis,
                             AsyncPlayerChatEvent e) {

        if (e.getPlayer() != p) return;
        if(e.getMessage().isEmpty()) return;

        e.setCancelled(true);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                onComplete.accept(p, e.getMessage()), 1);
        TaskID.cancel();
        lis.unregister();

    }

}