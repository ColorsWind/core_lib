package io.github.divios.core_lib.misc;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import io.github.divios.core_lib.Core_lib;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class confirmIH {

        private final Plugin plugin;
        private final Player p;
        private final BiConsumer<Player, Boolean> bi;
        private final ItemStack item;
        private final String title;
        private final String confirmLore;
        private final String cancelLore;
        private boolean backFlag = true;


        public static confirmIHBuilder builder() {
            return new confirmIHBuilder();
        }

        /**
         * @param p          Player to show the GUI
         * @param true_false Block of code to execute
         * @param title      Title of the GUI
         */

        @Deprecated
        public confirmIH(
                Plugin plugin,
                Player p,
                BiConsumer<Player, Boolean> true_false,
                ItemStack item,
                String title,
                String confirmLore,
                String cancelLore) {

            this.plugin = plugin;
            this.p = p;
            this.item = item.clone();
            bi = true_false;
            this.title = FormatUtils.color(title);
            this.confirmLore = confirmLore;
            this.cancelLore = cancelLore;
            openInventory();
        }

        public void openInventory() {

            InventoryGUI gui = new InventoryGUI(plugin, 27, title);

            IntStream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 19, 20, 21, 22, 23, 24, 25, 26).forEach(value -> {
                gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE)
                        .setName("&c"), e -> {
                }), value);
            });

            gui.addButton(ItemButton.create(new ItemBuilder(
                    ItemUtils.isEmpty(item) ? XMaterial.AIR.parseItem() : item), e -> {
            }), 13);

            IntStream.range(9, 13).forEach(value ->
                    gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                            .setName(confirmLore), (e) -> bi.accept(p, true)), value));

            IntStream.range(14, 18).forEach(value ->
                    gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                            .setName(cancelLore), (e) -> bi.accept(p, false)), value));

            gui.destroysOnClose();
            gui.open(p);
        }


    public static final class confirmIHBuilder {
        private final Plugin plugin = Core_lib.getPlugin();
        private Player p;
        private BiConsumer<Player, Boolean> bi;
        private ItemStack item;
        private String title = "";
        private String confirmLore = "";
        private String cancelLore = "";

        private confirmIHBuilder() {
        }

        public static confirmIHBuilder aconfirmIH() {
            return new confirmIHBuilder();
        }

        public confirmIHBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public confirmIHBuilder withAction(BiConsumer<Player, Boolean> bi) {
            this.bi = bi;
            return this;
        }

        public confirmIHBuilder withItem(ItemStack item) {
            this.item = item;
            return this;
        }

        public confirmIHBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public confirmIHBuilder withConfirmLore(String confirmLore) {
            this.confirmLore = confirmLore;
            return this;
        }

        public confirmIHBuilder withCancelLore(String cancelLore) {
            this.cancelLore = cancelLore;
            return this;
        }

        public confirmIH prompt() {

            Preconditions.checkNotNull(p, "player null");

            if (bi == null) bi = (p, b) -> {};
            if (item == null) item = XMaterial.AIR.parseItem();
            if (title == null) title = "";
            if (confirmLore == null) confirmLore = "&c";
            if (cancelLore == null) cancelLore = "&c";

            return new confirmIH(plugin, p, bi, item, title, confirmLore, cancelLore);
        }
    }
}
