package io.github.divios.core_lib;

import io.github.divios.core_lib.XCore.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.misc.Task;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Serializable;
import java.util.function.Consumer;

public final class Core_lib extends JavaPlugin {

    private static Core_lib PLUGIN;

    @Override
    public void onEnable() {
        PLUGIN = this;

        int pluginId = 11553; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);

        InventoryGUI gui = new InventoryGUI(27, "oke");
        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.CHEST)
                .setName("oke"), (ItemButton.SerializableConsumer<InventoryClickEvent>)
                e -> e.getWhoClicked().sendMessage("oke")), 11);

        new EventListener<>(PLUGIN, PlayerJoinEvent.class, e ->
                Task.syncDelayed(() -> gui.open(e.getPlayer()), 1L));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Core_lib getInstance() {
        return PLUGIN;
    }

}
