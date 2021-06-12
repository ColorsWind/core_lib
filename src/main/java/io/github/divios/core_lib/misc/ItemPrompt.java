package io.github.divios.core_lib.misc;

import io.github.divios.core_lib.itemutils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ItemPrompt{

    private final Plugin plugin;
    private final Player p;
    private final Task TaskID;
    public final BiConsumer<Player, ItemStack> onComplete;
    private final Consumer<Player> expiredAction;
    private final EventListener<PlayerInteractEvent> listener;

    public ItemPrompt(Plugin plugin,
                      Player p,
                      BiConsumer<Player, ItemStack> onComplete,
                      Consumer<Player> expiredAction,
                      String title,
                      String subTitle
    ) {
        this.plugin = plugin;
        this.p = p;
        this.onComplete = onComplete;
        this.expiredAction = expiredAction;

        listener = new EventListener<>(plugin, PlayerInteractEvent.class,
                EventPriority.HIGH, this::OnPlayerClick);

        TaskID = Task.syncDelayed(plugin, () -> {
            listener.unregister();
            expiredAction.accept(p);
        }, 200);

        Titles.sendTitle(p, 20, 60, 20,
                title, subTitle);
    }

    private void OnPlayerClick(PlayerInteractEvent e) {

        if (e.getPlayer() != p) return;

        e.setCancelled(true);

        if (ItemUtils.isEmpty(e.getItem())) {
            return;
        }

        ItemStack item = e.getItem().clone();
        item.setAmount(1);
        item.setDurability((short) 0);

        Titles.clearTitle(p);
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                        onComplete.accept(p, item)
                , 1L);

        TaskID.cancel();
        listener.unregister();

    }

}
