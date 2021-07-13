package io.github.divios.core_lib.inventory.builder;

import com.google.common.base.Preconditions;
import io.github.divios.core_lib.Core_lib;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class paginatedGui {

    private final String title;
    private final List<ItemButton> items;
    private final Pair<ItemStack, Integer> backButton;
    private final Pair<ItemStack, Integer> nextButton;
    private final PopulatorContentContext populator;

    private final List<InventoryGUI> invs = new ArrayList<>();

    private paginatedGui(
            String title,
            List<ItemButton> items,
            Pair<ItemStack, Integer> backButton,
            Pair<ItemStack, Integer> nextButton,

            PopulatorContentContext populator
    ) {
        this.title = title;
        this.items = items;
        this.backButton = backButton;
        this.nextButton = nextButton;
        this.populator = populator;

        init();
    }

    public void open(Player p) {
        invs.get(0).open(p);
    }

    public void open(Player p, int index) {
        if (index >= invs.size()) return;

        invs.get(index).open(p);
    }

    public List<InventoryGUI> getInvs() {
        return Collections.unmodifiableList(invs);
    }

    public InventoryGUI getInv(int index) {
        if (index >= invs.size()) return null;

        return invs.get(index);
    }

    private void init() {

        Inventory dummyInv = Bukkit.createInventory(null, 54, "");
        if (populator != null) populator.apply(dummyInv);
        dummyInv.setItem(backButton.get2(), backButton.get1());
        dummyInv.setItem(nextButton.get2(), nextButton.get1());

        int max = (int) Math.ceil( (float) items.size() / inventoryUtils.getEmptySlots(dummyInv));

        for (int i = 0; i < max; i++) {         // initial population

            InventoryGUI invGui = new InventoryGUI(Core_lib.getPlugin(), 54, title);
            Inventory inv = invGui.getInventory();

            if (populator != null) populator.apply(inv);     // Apply populator
            if (i != 0) {
                int finalI = i;
                invGui.addButton(backButton.get2(), ItemButton.create(backButton.get1(),
                        e -> invs.get(finalI - 1).open((Player) e.getWhoClicked())));
            }

            if (i != max - 1) {
                int finalI = i;
                invGui.addButton(nextButton.get2(), ItemButton.create(nextButton.get1(),
                        e -> invs.get(finalI + 1).open((Player) e.getWhoClicked())));
            }

            invGui.setDestroyOnClose(false);
            invs.add(invGui);
        }

        InventoryGUI inv = invs.get(0);         // Populate with items
        for (ItemButton item : items) {

            int slot;
            if ((slot = inventoryUtils.getFirstEmpty(inv.getInventory())) == -1) {
                inv = invs.get(invs.indexOf(inv) + 1);
                slot = inventoryUtils.getFirstEmpty(inv.getInventory());
            }

            inv.addButton(slot, item);

        }

    }

    public void destroy() {
        invs.forEach(InventoryGUI::destroy);
    }

    public static paginatedGuiBuilder Builder() {
        return new BuilderImpl();
    }


    protected static final class BuilderImpl implements paginatedGuiBuilder {
        private String title;
        private List<ItemButton> items;
        private Pair<ItemStack, Integer> backButton;
        private Pair<ItemStack, Integer> nextButton;
        private PopulatorContentContext populator;

        BuilderImpl() {}

        @Override
        public paginatedGuiBuilder withTitle(String title) {
            this.title = title;
            return this;
        }


        public paginatedGuiBuilder withItems(List<ItemButton> items) {
            this.items = items;
            return this;
        }


        public paginatedGuiBuilder withItems(Stream<ItemButton> items) {
            this.items = items.collect(Collectors.toList());
            return this;
        }

        @Override
        public paginatedGuiBuilder withBackButton(ItemStack item, int slot) {
            this.backButton = Pair.of(item, slot);
            return this;
        }

        @Override
        public paginatedGuiBuilder withNextButton(ItemStack item, int slot) {
            this.nextButton = Pair.of(item, slot);
            return this;
        }

        @Override
        public paginatedGuiBuilder withPopulator(inventoryPopulatorState populator) {
            this.populator = populator.restore();
            return this;
        }

        @Override
        public paginatedGuiBuilder withPopulator(PopulatorContentContext populator) {
            this.populator = populator;
            return this;
        }

        @Override
        public paginatedGui build() {

            Preconditions.checkNotNull(items, "items null");
            Preconditions.checkNotNull(backButton, "backButton null");
            Preconditions.checkNotNull(nextButton, "nextButton null");

            Preconditions.checkArgument(!items.isEmpty(), "items is empty");
            Preconditions.checkArgument(!ItemUtils.isEmpty(backButton.get1()), "backbutton is empty");
            Preconditions.checkArgument(!ItemUtils.isEmpty(backButton.get1()), "nextbutton is empty");
            Preconditions.checkArgument(backButton.get2() >= 0 && backButton.get2() < 54,
                    "Backbutton slot out of bounds");
            Preconditions.checkArgument(nextButton.get2() >= 0 && nextButton.get2() < 54,
                    "nextButton slot out of bounds");

            return new paginatedGui(title, items, backButton, nextButton, populator);
        }
    }
}
