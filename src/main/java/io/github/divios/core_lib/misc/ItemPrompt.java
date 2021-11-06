package io.github.divios.core_lib.misc;

import com.cryptomorin.xseries.messages.Titles;
import com.google.common.base.Preconditions;
import io.github.divios.core_lib.Core_lib;
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
    private final BiConsumer<Player, ItemStack> onComplete;
    private final Consumer<Player> expiredAction;
    private EventListener<PlayerInteractEvent> listener;

    public static ItemPromptBuilder builder() {
        return new ItemPromptBuilder();
    }

    @Deprecated
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

        p.closeInventory();
        Task.syncDelayed(plugin, () -> listener = new EventListener<>(PlayerInteractEvent.class,
                EventPriority.HIGH, this::OnPlayerClick), 3L);

        TaskID = Task.syncDelayed(plugin, () -> {
            listener.unregister();
            this.expiredAction.accept(p);
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

    public static final class ItemPromptBuilder {

        private final Plugin plugin = Core_lib.PLUGIN;
        private Player p;
        private Consumer<ItemStack> onComplete;
        private Runnable expiredAction;
        private String title;
        private String subTitle;

        private ItemPromptBuilder() {}

        public ItemPromptBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public ItemPromptBuilder withComplete(Consumer<ItemStack> onComplete) {
            this.onComplete = onComplete;
            return this;
        }

        public ItemPromptBuilder withExpiredAction(Runnable expiredAction) {
            this.expiredAction = expiredAction;
            return this;
        }

        public ItemPromptBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public ItemPromptBuilder withSubtitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public ItemPrompt build() {

            Preconditions.checkNotNull(p, "player is null");
            if (onComplete == null) onComplete = (i) -> {};
            if (expiredAction == null) expiredAction = () -> {};
            if (title == null) title = "";
            if (subTitle == null) subTitle = "";

            return new ItemPrompt(plugin, p, (p, i) -> onComplete.accept(i),
                    (p) -> expiredAction.run(), FormatUtils.color(title), FormatUtils.color(subTitle));
        }
    }
}
