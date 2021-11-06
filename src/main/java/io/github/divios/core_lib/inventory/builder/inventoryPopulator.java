package io.github.divios.core_lib.inventory.builder;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.utils.Primitives;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class inventoryPopulator {

    private static final List<ItemStack> STAINED_GLASS = Arrays.asList(
            new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.ORANGE_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.LIME_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.PINK_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.CYAN_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.PURPLE_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.BLUE_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.BROWN_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem()).setName("&c"),
            new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()).setName("&c")
            );

    protected inventoryPopulator (
            Inventory inv,
            List<ItemStack> toFill,
            List<List<Integer>> mask,
            List<List<Integer>> scheme
    ) {

        // Preconditions
        Objects.requireNonNull(inv, "inv null");
        Objects.requireNonNull(toFill, "toFill null");
        Objects.requireNonNull(mask, "mask null");
        Objects.requireNonNull(scheme, "scheme null");
        Preconditions.checkArgument(!toFill.isEmpty(), "toFill is empty");
        Preconditions.checkArgument(inv.getSize() / 9 <= mask.size(),
                "mask size");


        for (int i = 0; i < mask.size(); i++) {

            int count = 0;
            for (int j = 0; j < 9; j++) {

                if (count >= scheme.get(i).size()) break;

                if (mask.get(i).get(j) == 0) continue;

                inv.setItem(i * 9 + j, toFill.get(scheme.get(i).get(count)));
                count++;

            }
        }

    }

    public static PopulatorItemContext builder() {
        return new PopulatorItemContextImpl();
    }


    protected static final class PopulatorItemContextImpl implements PopulatorItemContext{

        private PopulatorItemContextImpl() {}

        public PopulatorContentContext ofGlass() {
            return new PopulatorContentContextImpl(STAINED_GLASS);
        }

        public PopulatorContentContext of(ItemStack item) {
            List<ItemStack> toFill = new ArrayList<>();
            IntStream.range(0, 9).forEach(value -> toFill.add(item));
            return new PopulatorContentContextImpl(toFill);
        }

        public PopulatorContentContext of(List<ItemStack> toFill) {
            Objects.requireNonNull(toFill, "toFill MaskContext null");
            Preconditions.checkArgument(toFill.stream().noneMatch(Objects::isNull),
                    "toFill MaskContext null index");
            Preconditions.checkArgument(!toFill.isEmpty(), "empty to fill");

            if (toFill.size() < 9)
                IntStream.range(0, 9 - toFill.size())
                        .forEach(value -> toFill.add(toFill.get(0)));


            return new PopulatorContentContextImpl(toFill);
        }

    }

    protected static final class PopulatorContentContextImpl implements PopulatorContentContext{

        private final List<ItemStack> toFill;
        private List<List<Integer>> masks = new ArrayList<>();
        private List<List<Integer>> schemes = new ArrayList<>();

        protected PopulatorContentContextImpl(
                List<ItemStack> toFill,
                List<List<Integer>> masks,
                List<List<Integer>> schemes
        ) {
            this.toFill = toFill;
            this.masks = masks;
            this.schemes = schemes;
        }

        private PopulatorContentContextImpl(List<ItemStack> toFill) {
            this.toFill = toFill;
        }

        public PopulatorContentContext mask(String mask) {

            Preconditions.checkArgument(mask.length() == 9, "Mask length");
            Preconditions.checkArgument(Primitives.isInteger(mask), "Not integer");

            if (masks.size() == 9) return this;

            List<Integer> innerMask = Arrays.stream(mask.split(""))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            masks.add(innerMask);
            return this;
        }

        public PopulatorContentContext scheme(int... scheme) {

            Preconditions.checkNotNull(scheme, "scheme null");
            Preconditions.checkArgument(scheme.length != 0, "scheme is empty");
            Preconditions.checkArgument(scheme.length <= 9, "scheme length");


            schemes.add(Ints.asList(scheme));

            return this;
        }

        public void apply(Inventory inv) {
            new inventoryPopulator(inv, toFill, masks, schemes);
        }

        public inventoryPopulatorState toState() {
            return new inventoryPopulatorStateImpl(toFill, masks, schemes);
        }

    }

    protected static final class inventoryPopulatorStateImpl implements inventoryPopulatorState {

        private final List<ItemStack> toFill;
        private final List<List<Integer>> masks;
        private final List<List<Integer>> schemes;

        private inventoryPopulatorStateImpl(
                List<ItemStack> toFill,
                List<List<Integer>> masks,
                List<List<Integer>> schemes
        ) {
            this.toFill = toFill;
            this.masks = masks;
            this.schemes = schemes;
        }

        public List<ItemStack> getToFill() {
            return Collections.unmodifiableList(toFill);
        }

        public List<List<Integer>> getMasks() {
            return Collections.unmodifiableList(masks);
        }

        public List<List<Integer>> getSchemes() {
            return Collections.unmodifiableList(schemes);
        }

        public PopulatorContentContext restore() {
            return new PopulatorContentContextImpl(toFill, masks, schemes);
        }

        public static inventoryPopulatorStateImpl empty() {
            return new inventoryPopulatorStateImpl(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

    }


}
