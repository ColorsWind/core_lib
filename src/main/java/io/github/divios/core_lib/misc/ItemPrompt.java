package io.github.divios.core_lib.misc;

import io.github.divios.core_lib.Core_lib;
import io.github.divios.core_lib.itemutils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ItemPrompt{

    private static final Core_lib main = Core_lib.getInstance();

    private final Player p;
    private final BukkitTask TaskID;
    public final BiConsumer<Player, ItemStack> onComplete;
    private final Consumer<Player> expiredAction;
    private final EventListener<PlayerInteractEvent> listener;

    public ItemPrompt(Player p,
                      BiConsumer<Player, ItemStack> onComplete,
                      Consumer<Player> expiredAction,
                      String title,
                      String subTitle
    ) {

        this.p = p;
        this.onComplete = onComplete;
        this.expiredAction = expiredAction;

        listener = new EventListener<PlayerInteractEvent>(main, PlayerInteractEvent.class,
                EventPriority.HIGH, this::OnPlayerClick);

        TaskID = Bukkit.getScheduler().runTaskLater(main, () -> {
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
        Bukkit.getScheduler().runTaskLater(main, () ->
                        onComplete.accept(p, item)
                , 1L);

        Bukkit.getScheduler().cancelTask(TaskID.getTaskId());
        listener.unregister();

    }

}
