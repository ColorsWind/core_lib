package io.github.divios.core_lib.inventory;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class materialsPrompt {

    private static final List<ItemStack> contents;
    private final List<InventoryGUI> inv = new LinkedList<>();

    private final Plugin plugin;
    private final Player p;
    private final BiConsumer<Boolean, XMaterial> consumer;

    static {
        contents = removeGarbageMaterial();
    }

    private materialsPrompt(Plugin plugin, Player p,
                            BiConsumer<Boolean, XMaterial> consumer
    ) {
        this.plugin = plugin;
        this.p = p;
        this.consumer = consumer;

        createInvs();
        inv.get(0).open(p);
    }

    public static void open(Plugin plugin, Player p, BiConsumer<Boolean, XMaterial> consumer) {
        new materialsPrompt(plugin, p, consumer);
    }

    private void createInvs() {
        IntStream.range(0, (int) Math.ceil(contents.size() / 32D))
                .forEach(value -> inv.add(new InventoryGUI(plugin, 54, "Materials")));

        inv.parallelStream().forEach(inventoryGUI ->
                createInv(inv.indexOf(inventoryGUI), inventoryGUI));

    }

    private void createInv(int index, InventoryGUI inventoryGUI) {

            inventoryGUI.setDestroyOnClose(false);

            IntStream.of(0, 1, 9, 7, 8, 17, 45, 46, 36, 52, 53, 44).forEach(value ->
                    inventoryGUI.addButton(new ItemButton(
                            new ItemBuilder(XMaterial.BROWN_STAINED_GLASS_PANE)
                            .setName("&c"), e -> {
                    }), value));

            IntStream.of(2, 6, 47, 51).forEach(value ->
                    inventoryGUI.addButton(new ItemButton(
                            new ItemBuilder(XMaterial.ORANGE_STAINED_GLASS_PANE)
                            .setName("&c"), e -> {
                    }), value));

            IntStream.of(3, 4, 5, 48, 49, 50).forEach(value ->
                    inventoryGUI.addButton(new ItemButton(
                            new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE)
                            .setName("&c"), e -> {
                    }), value));

            if (index != inv.size() - 1) {      // next buttom
                inventoryGUI.addButton(new ItemButton(
                        new ItemBuilder(XMaterial.PLAYER_HEAD)
                        .setName("&1Next").applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf"),
                        e -> inv.get(index + 1).open(p)), 51);
            }

            if (index != 0) {                   // previous buttom
                inventoryGUI.addButton(new ItemButton(
                        new ItemBuilder(XMaterial.PLAYER_HEAD)
                                .setName("&1Previous").applyTexture("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9"),
                        e -> inv.get(index - 1).open(p)), 47);
            }

            inventoryGUI.addButton(new ItemButton(new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setName("&cReturn").setLore("&7Click to return")
                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                    , e -> {
                Schedulers.sync().runLater( this::destroyAll, 3L);
                consumer.accept(false, null);
            }), 8);

            for (int i = index * 32; i < ( index + 1) * 32; i++) {

            int slot = inventoryUtils.getFirstEmpty(inventoryGUI.getInventory());

            if (slot == -1 || i >= contents.size()) break;
                inventoryGUI.addButton(new ItemButton(new ItemBuilder(contents.get(i))
                        .setName("&f&l" + ItemUtils.getName(contents.get(i))),
                        e -> {
                            Schedulers.sync().runLater( this::destroyAll, 3L);
                            consumer.accept(true, XMaterial.matchXMaterial(e.getCurrentItem()));
                        }), slot);
            }
    }

    private void destroyAll() {
        inv.forEach(InventoryGUI::destroy);
    }

    private static List<ItemStack> removeGarbageMaterial() {
        Inventory inv = Bukkit.createInventory(null, 54, "");
        List<ItemStack> materialsaux = new ArrayList<>();

        for (XMaterial m : XMaterial.values()) {
            ItemStack item = m.parseItem();
            inv.setItem(0, item);
            boolean err = false;
            try {
                inv.getItem(0).getType();
            } catch (NullPointerException e) {
                err = true;
            }
            if (!err) {
                item = new ItemBuilder(item).setName("&f&l" + m);
                materialsaux.add(item);
            }
        }
        return materialsaux;
    }

}