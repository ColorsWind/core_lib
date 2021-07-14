package io.github.divios.core_lib.itemutils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A utility class to easily create items
 * @author Redempt
 *
 */
public class ItemBuilder extends ItemStack {

    /**
     * Constructs a new ItemBuilder. An ItemBuilder extends ItemStack, an can be used as such.
     * @param material The type of the item
     * @param amount The amount of the item
     */
    public ItemBuilder(Material material, int amount) {
        super(material, amount);
    }

    /**
     * Constructs a new ItemBuilder. An ItemBuilder extends ItemStack, an can be used as such.
     * @param material The type of the item
     */
    public ItemBuilder(Material material) {
        super(material);
    }

    /**
     * Constructs an ItemBuilder using a pre-existing item
     * @param item The item to copy
     */
    public ItemBuilder(ItemStack item) {
        super(item);
    }

    /**
     * Constructs an ItemBuilder using XMaterial
     * @param xmaterial The type of the item
     */
    public ItemBuilder(XMaterial xmaterial) { super(xmaterial.parseItem()); }

    public static ItemBuilder of(XMaterial xMaterial) { return new ItemBuilder(xMaterial); }

    public static ItemBuilder of(ItemStack item) { return new ItemBuilder(item); }

    /**
     * Sets the stack size of this ItemBuilder
     * @param amount The number of items in the stack
     * @return The ItemBuilder with the new stack size
     */
    public ItemBuilder setCount(int amount) {
        ItemBuilder clone = new ItemBuilder(this.clone());
        clone.setAmount(amount);
        return clone;
    }

    /**
     * Adds an enchantment to this ItemBuilder
     * @param enchant The enchantment to add
     * @param level The level of the enchantment
     * @return The enchanted ItemBuilder
     */
    public ItemBuilder addEnchant(Enchantment enchant, int level) {
        return new ItemBuilder(ItemUtils.addEnchant(this, enchant, level));
    }

    public ItemBuilder removeEnchant(Enchantment enchantment) {
        return new ItemBuilder(ItemUtils.removeEnchant(this, enchantment));
    }

    /**
     * Set the lore of this ItemBuilder
     * @param lore The lines of lore
     * @return The ItemBuilder with lore added
     */
    public ItemBuilder setLore(String... lore) {
        return new ItemBuilder(ItemUtils.setLore(this, lore));
    }

    /**
     * Set the lore of this ItemBuilder
     * @param lore The lines of lore
     * @return The ItemBuilder with lore added
     */
    public ItemBuilder setLore(List<String> lore) {
        return new ItemBuilder(ItemUtils.setLore(this, lore));
    }

    /**
     * Add a line of lore to this ItemBuilder
     * @param line The line of lore
     * @return The ItemBuilder with lore added
     */
    public ItemBuilder addLore(String line) {
        return new ItemBuilder(ItemUtils.addLore(this, line));
    }

    /**
     * Add multiple lines of lore to this ItemBuilder
     * @param lines The lines of lore
     * @return The ItemBuilder with lore added
     */
    public ItemBuilder addLore(String... lines) {
        return new ItemBuilder(ItemUtils.addLore(this, lines));
    }

    /**
     * Add multiple lines of lore to this ItemBuilder
     * @param lines The lines of lore
     * @return The ItemBuilder with lore added
     */
    public ItemBuilder addLore(List<String> lines) {
        return new ItemBuilder(ItemUtils.addLore(this, lines));
    }

    public ItemBuilder addLorewithPlaces(List<String> lines, Function<String, String> placeholders) {
        return new ItemBuilder(ItemUtils.addLorewithPlaces(this, lines, placeholders)); }

    public ItemBuilder addLorewithPlaces(String lines, Function<String, String> placeholders) {
        return addLorewithPlaces(Collections.singletonList(lines), placeholders); }

    /**
     * Renames this ItemBuilder
     * @param name The name to set
     * @return The renamed ItemBuilder
     */
    public ItemBuilder setName(String name) {
        return new ItemBuilder(ItemUtils.rename(this, name));
    }

    public ItemBuilder setEmptyName() { return new ItemBuilder(ItemUtils.rename(this, "&c")); }

    public ItemBuilder setMaterial(Material m) { return new ItemBuilder(ItemUtils.setMaterial(this, m)); }

    public ItemBuilder setMaterial(XMaterial m) { return setMaterial(m.parseMaterial()); }

    /**
     * Set the durability (damage) of the ItemBuilder
     * @param durability The durability to set
     * @return The ItemBuilder with its durability changed
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setDurability(int durability) {
        this.setDurability((short) durability);
        return new ItemBuilder(this);
    }

    /**
     * Adds ItemFlags to this ItemBuilder
     * @param flags The ItemFlags to add
     * @return The ItemBuilder with the flags added
     */
    public ItemBuilder addItemFlags(ItemFlag... flags) {
        return new ItemBuilder(ItemUtils.addItemFlags(this, flags));
    }

    /**
     * Adds ItemFlags to this ItemBuilder
     * @param flags The ItemFlags to add
     * @return The ItemBuilder with the flags added
     */
    public ItemBuilder addItemFlags(List<ItemFlag> flags) { return new ItemBuilder(ItemUtils.addItemFlags(this, flags));
    }

    /**
     * Removes ItemFlags from this ItemBuilder
     * @param flags The ItemFlags to remove
     * @return The ItemBuilder with the flags removed
     */
    public ItemBuilder removeItemFlags(ItemFlag... flags) { return new ItemBuilder(ItemUtils.removeItemFlags(this, flags)); }

    /**
     * Removes ItemFlags from this ItemBuilder
     * @param flags The ItemFlags to remove
     * @return The ItemBuilder with the flags removed
     */
    public ItemBuilder removeItemFlags(List<ItemFlag> flags) { return new ItemBuilder(ItemUtils.removeItemFlags(this, flags)); }

    /**
     * Sets this ItemBuilder to be unbreakable
     * @return The ItemBuilder with the unbreakable tag added
     */
    public ItemBuilder unbreakable() {
        return new ItemBuilder(ItemUtils.setUnbreakable(this));
    }

    /**
     * Applies the given texture
     * @param url
     * @return
     */
    public ItemBuilder applyTexture(String url) { return new ItemBuilder(ItemUtils.applyTexture(this, url)); }

    public ItemBuilder applyTexture(UUID uuid) { return new ItemBuilder(ItemUtils.applyTexture(this, uuid)); }

    public void ifPresent(Consumer<ItemStack> consumer) {
        if (!ItemUtils.isEmpty(this)) consumer.accept(this);
    }

}