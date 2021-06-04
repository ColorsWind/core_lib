package io.github.divios.core_lib.inventory;

import io.github.divios.core_lib.XCore.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.Task;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class materialsPrompt {

    private static final List<ItemStack> contents;

    private final Plugin plugin;
    private final Player p;
    private final BiConsumer<Boolean, Material> consumer;

    static {
        contents = removeGarbageMaterial();
    }

    private materialsPrompt(Plugin plugin, Player p,
                           BiConsumer<Boolean, Material> consumer
    ) {
        this.plugin = plugin;
        this.p = p;
        this.consumer = consumer;
    }

    public static void open(Plugin plugin, Player p, BiConsumer<Boolean, Material> consumer) {

        materialsPrompt instance = new materialsPrompt(plugin, p, consumer);
        new dynamicGui.Builder()
                .contents(instance::contents)
                .contentAction(instance::contentActions)
                .back(instance::backAction)
                .plugin(plugin)
                .preventClose()
                .open(p).getinvs();
    }

    private List<ItemStack> contents() {
        return contents;
    }

    private dynamicGui.Response contentActions(InventoryClickEvent e) {
        Task.syncDelayed(plugin, () -> consumer.accept(true,
                XMaterial.matchXMaterial(e.getCurrentItem()).parseMaterial())
                , 1L);
        return dynamicGui.Response.close();
    }

    private static List<ItemStack> removeGarbageMaterial(){
        Inventory inv = Bukkit.createInventory(null, 54, "");
        List<ItemStack> materialsaux = new ArrayList<>();

        for (XMaterial m: XMaterial.values()) {
            ItemStack item = m.parseItem();
            inv.setItem(0, item);
            boolean err = false;
            try{
                inv.getItem(0).getType();
            } catch (NullPointerException e) {
                err = true;
            }
            if(!err) {
                item = new ItemBuilder(item).setName("&f&l" + m.toString());
                materialsaux.add(item);
            }

        }
        return materialsaux;
    }

    public void backAction(Player p) {
        consumer.accept(false, null);
    }

}