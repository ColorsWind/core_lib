package io.github.divios.core_lib.itemutils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import com.mojang.authlib.GameProfile;
import de.tr7zw.nbtapi.NBTItem;
import io.github.divios.core_lib.misc.FormatUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import com.mojang.authlib.properties.Property;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A utility class to easily modify items
 *
 * @author Redempt
 */
public class ItemUtils {

    /**
     * Renames an ItemStack, functionally identical to {@link ItemUtils#setName(ItemStack, String)} but kept for legacy reasons
     *
     * @param item The ItemStack to be renamed
     * @param name The name to give the ItemStack
     * @return The renamed ItemStack
     */

    public static ItemStack rename(ItemStack item, String name) {
        ItemMeta meta = getMetadata(item);
        meta.setDisplayName(FormatUtils.color(name));
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Renames an ItemStack
     *
     * @param item The ItemStack to be renamed
     * @param name The name to give the ItemStack
     * @return The renamed ItemStack
     */
    public static ItemStack setName(ItemStack item, String name) {
        if (name == null) return item;
        return rename(item, name);
    }

    /**
     * Returns the ItemStack display name
     *
     * @param item The ItemStack to get the name
     * @return The name of the ItemStack
     */
    public static String getName(ItemStack item) {
        ItemMeta meta = getMetadata(item);

        return meta.hasDisplayName()
                ? meta.getDisplayName()
                : getPrettyMaterialName(getMaterial(item));
    }

    private static String getPrettyMaterialName(Material m) {
        String CURSIVE = "&f";
        String nameSpace = m.name().toLowerCase(Locale.ROOT).replaceAll("_", " ");
        String capitalizeNameSpace = capitalizeString(nameSpace);

        return ChatColor.translateAlternateColorCodes('&', CURSIVE + capitalizeNameSpace);
    }

    private static String capitalizeString(String s) {
        String[] arr = s.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String value : arr) {
            sb.append(Character.toUpperCase(value.charAt(0)))
                    .append(value.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public static ItemStack setAmount(ItemStack item, int amount) {
        ItemStack cloned = item.clone();
        cloned.setAmount(amount);
        return cloned;
    }

    public static int getAmount(ItemStack item) {
        return item.getAmount();
    }

    public static ItemStack setMaterial(ItemStack item, Material m) {
        ItemStack cloned = item.clone();
        cloned.setType(m);
        return cloned;
    }

    public static ItemStack setMaterial(ItemStack item, XMaterial m) {
        ItemStack cloned = item.clone();
        cloned.setType(m.parseMaterial());
        cloned.setData(m.parseItem().getData());
        return cloned;
    }

    public static Material getMaterial(ItemStack item) {
        return item.getType();
    }

    /**
     * Set a single line of lore for an ItemStack
     *
     * @param item The ItemStack to be given lore
     * @param line The line of lore to be given
     * @return The modified ItemStack
     */
    public static ItemStack setLore(ItemStack item, String line) {
        return setLore(item, Collections.singletonList(line));
    }

    /**
     * Returns the lore of the item. If the item has not lore,
     * returns a new List
     *
     * @param item ItemStack to get the lore
     * @return The lore of the ItemStack
     */
    public static List<String> getLore(ItemStack item) {
        return getMetadata(item).getLore() == null ? new ArrayList<>() :
                getMetadata(item).getLore();
    }

    /**
     * Set multiple lines of lore for an ItemStack
     *
     * @param item The ItemStack to be given lore
     * @param lore The lines of lore to be given
     * @return The modified ItemStack
     */
    public static ItemStack setLore(ItemStack item, List<String> lore) {
        ItemMeta meta = getMetadata(item);
        meta.setLore(lore.stream().map(FormatUtils::color).collect(Collectors.toList()));
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Set multiple lines of lore for an ItemStack
     *
     * @param item The ItemStack to be given lore
     * @param lore The lines of lore to be given
     * @return The modified ItemStack
     */
    public static ItemStack setLore(ItemStack item, String... lore) {
        return setLore(item, Arrays.asList(lore));
    }

    /**
     * Add a line of lore to an ItemStack
     *
     * @param item The ItemStack to be given lore
     * @param line The line of lore to add
     * @return The modified ItemStack
     */
    public static ItemStack addLore(ItemStack item, String line) {
        return addLore(item, Collections.singletonList(line));
    }

    /**
     * Adds multiple lines of lore to an ItemStack
     *
     * @param item  The ItemStack to be given lore
     * @param lines The lines or lore to add
     * @return The modified ItemStack
     */
    public static ItemStack addLore(ItemStack item, String... lines) {
        return addLore(item, Arrays.asList(lines));
    }

    /**
     * Adds multiple lines of lore to an ItemStack
     *
     * @param item  The ItemStack to be given lore
     * @param lines The lines or lore to add
     * @return The modified ItemStack
     */
    public static ItemStack addLore(ItemStack item, List<String> lines) {
        ItemMeta meta = getMetadata(item);
        List<String> lore = meta.getLore();
        lore = lore == null ? new ArrayList<>() : lore;
        lore.addAll(lines.stream().map(FormatUtils::color).collect(Collectors.toList()));
        meta.setLore(lore);
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    public static ItemStack addLorewithPlaces(ItemStack item,
                                              List<String> lines, Function<String, String> placeholders) {

        final ItemStack[] toReturn = {item.clone()};
        Pattern regex = Pattern.compile("\\{(.*)\\}");

        lines.forEach(s -> {
            AtomicReference<String> aux = new AtomicReference<>(s);
            Matcher matcher = regex.matcher(s);

            while (matcher.find()) {
                String match = matcher.group(1);
                String placeH = placeholders.apply(match);
                if (placeH == null) continue;
                aux.set(s.replace("{" + match + "}", placeH));
            }

            toReturn[0] = addLore(toReturn[0], aux.get());
        });

        return toReturn[0];
    }

    /**
     * Removes the lore of an ItemStack
     *
     * @param item The ItemStack which lore to be removed
     * @param line the line to be removed
     * @return ItemStack with the lore removed
     */
    public static ItemStack removeLore(ItemStack item, int line) {
        ItemStack cloned = item.clone();
        List<String> lore = getLore(cloned);
        if (line < lore.size()) lore.remove(line);

        return setLore(cloned, lore);
    }

    /**
     * Sets an item to be unbreakable
     *
     * @param item The item to make unbreakable
     * @return The unbreakable item
     */
    public static ItemStack setUnbreakable(ItemStack item) {
        item = item.clone();
        ItemMeta meta = getMetadata(item);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setDurability(ItemStack item, short durability) {
        ItemStack cloned = item.clone();
        cloned.setDurability(durability);
        return cloned;
    }

    public static short setDurability(ItemStack item) {
        return item.getDurability();
    }

    /**
     * Add an enchantment to an ItemStack
     *
     * @param item    The ItemStack to be enchanted
     * @param enchant The Enchantment to add to the ItemStack
     * @param level   The level of the Enchantment
     * @return The enchanted ItemStack
     */
    public static ItemStack addEnchant(ItemStack item, Enchantment enchant, int level) {
        ItemMeta meta = getMetadata(item);
        meta.addEnchant(enchant, level, true);
        if (level == 0) {
            meta.removeEnchant(enchant);
        }
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    public static ItemStack removeEnchant(ItemStack item, Enchantment enchant) {
        ItemMeta meta = getMetadata(item);
        meta.removeEnchant(enchant);

        ItemStack cloned = item.clone();
        cloned.setItemMeta(meta);
        return cloned;
    }

    public static boolean isPotion(ItemStack item) {
        Material material = getMaterial(item);
        return XPotion.canHaveEffects(material);
    }

    public static PotionMeta getPotionMeta(ItemStack item) {
        if (!isPotion(item)) return null;
        try {
            return (PotionMeta) getMetadata(item);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean hasPotionEffect(ItemStack item, PotionEffect effect) {
        PotionMeta meta;
        if ((meta = getPotionMeta(item)) == null) return false;

        return meta.hasCustomEffect(effect.getType());
    }

    public static List<PotionEffect> getAllPotionEffects(ItemStack item) {
        PotionMeta meta;
        if ((meta = getPotionMeta(item)) == null) return null;

        return meta.getCustomEffects();
    }

    public static ItemStack addPotionEffects(ItemStack item, PotionEffect... effects) {
        return addPotionEffects(item, Arrays.asList(effects));
    }

    public static ItemStack addPotionEffects(ItemStack item, Collection<PotionEffect> effects) {
        PotionMeta meta;
        if ((meta = getPotionMeta(item)) == null) return item;

        effects.forEach(potionEffect -> meta.addCustomEffect(potionEffect, true));

        ItemStack cloned = item.clone();
        cloned.setItemMeta(meta);

        return cloned;
    }

    public static ItemStack removePotionEffects(ItemStack item, PotionEffect... effects) {
        return removePotionEffects(item, Arrays.asList(effects));
    }

    public static ItemStack removePotionEffects(ItemStack item, Collection<PotionEffect> effects) {
        PotionMeta meta;
        if ((meta = getPotionMeta(item)) == null) return item;

        effects.forEach(potionEffect -> meta.removeCustomEffect(potionEffect.getType()));

        ItemStack cloned = item.clone();
        cloned.setItemMeta(meta);

        return cloned;
    }

    public static boolean hasItemFlags(ItemStack item, ItemFlag flag) {
        return getMetadata(item).hasItemFlag(flag);
    }

    public static boolean hasItemFlags(ItemStack item, ItemFlag... flags) {
        ItemMeta meta = getMetadata(item);
        for (ItemFlag flag : flags)
            if (!meta.hasItemFlag(flag)) return false;

        return true;
    }

    /**
     * Adds ItemFlags to the item
     *
     * @param item  The item to add ItemFlags to
     * @param flags The ItemFlags to add
     * @return The modified item
     */
    public static ItemStack addItemFlags(ItemStack item, ItemFlag... flags) {
        return addItemFlags(item, Arrays.asList(flags));
    }

    /**
     * Adds ItemFlags to the item
     *
     * @param item  The item to add ItemFlags to
     * @param flags The ItemFlags to add
     * @return The modified item
     */
    public static ItemStack addItemFlags(ItemStack item, List<ItemFlag> flags) {
        ItemMeta meta = getMetadata(item);
        flags.forEach(meta::addItemFlags);
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Removes ItemFlags from the item
     *
     * @param item  The item to add ItemFlags to
     * @param flags The ItemFlags to add
     * @return The modified item
     */
    public static ItemStack removeItemFlags(ItemStack item, ItemFlag... flags) {
        return removeItemFlags(item, Arrays.asList(flags));
    }

    /**
     * Removes ItemFlags from the item
     *
     * @param item  The item to add ItemFlags to
     * @param flags The ItemFlags to add
     * @return The modified item
     */
    public static ItemStack removeItemFlags(ItemStack item, List<ItemFlag> flags) {
        ItemMeta meta = getMetadata(item);
        flags.forEach(meta::removeItemFlags);
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Sets NBTData
     *
     * @param item
     * @param key
     * @param o
     * @return
     */
    public static ItemStack setNBTData(ItemStack item, String key, Object o) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setObject(key, o);
        return nbtItem.getItem();
    }

    /**
     * Returns if the item has the key
     *
     * @param item
     * @param key
     * @return
     */
    public static boolean hasNBTData(ItemStack item, String key) {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey(key);
    }

    /**
     * Returns the object on the key
     *
     * @param item
     * @param key
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T getNBTData(ItemStack item, String key, java.lang.Class<T> type) {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.getObject(key, type);
    }

    /**
     * Applies a texture to the item. Must be PLAYER_HEAD
     *
     * @param item
     * @param url
     * @return
     */
    public static ItemStack applyTexture(ItemStack item, String url) {
        if (!item.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial()))
            return item;

        return XSkull.of(item).profile(Profileable.of(ProfileInputType.TEXTURE_URL, url)).apply();
    }

    public static ItemStack applyTexture(ItemStack item, UUID uuid) {
        if (!item.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial()))
            return item;

        return XSkull.of(item).profile(Profileable.of(uuid)).apply();
    }

    public static ItemMeta getMetadata(ItemStack item) {
        return item.getItemMeta() == null ?
                Bukkit.getItemFactory().getItemMeta(item.getType()) : item.getItemMeta();
    }

    public static ItemStack setItemMeta(ItemStack item, ItemMeta meta) {
        ItemStack clone = item.clone();
        item.setItemMeta(meta);
        return clone;
    }

    /**
     * Counts the number of the given item in the given inventory
     *
     * @param inv        The inventory to count the items in
     * @param item       The item to count
     * @param comparison A filter to compare items for counting
     * @return The number of items found
     */
    public static int count(Inventory inv, ItemStack item, BiPredicate<ItemStack, ItemStack> comparison) {
        int count = 0;
        for (ItemStack i : inv) {
            if (comparison.test(item, i)) {
                count += i.getAmount();
            }
        }
        return count;
    }

    /**
     * Counts the number of the given item in the given inventory
     *
     * @param inv  The inventory to count the items in
     * @param item The item to count
     * @return The number of items found
     */
    public static int count(Inventory inv, ItemStack item) {
        return count(inv, item, ItemStack::isSimilar);
    }

    /**
     * Counts the number of items of the given type in the given inventory
     *
     * @param inv  The inventory to count the items in
     * @param type The type of item to count
     * @return The number of items found
     */
    public static int count(Inventory inv, Material type) {
        return count(inv, new ItemStack(type), (a, b) -> compare(a, b, ItemTrait.TYPE));
    }

    /**
     * Removes the specified amount of the given item from the given inventory
     *
     * @param inv        The inventory to remove the items from
     * @param item       The item to be removed
     * @param amount     The amount of items to remove
     * @param comparison A filter to compare items for removal
     * @return Whether the amount specified could be removed. False if it removed less than specified.
     */
    public static boolean remove(Inventory inv, ItemStack item, int amount, BiPredicate<ItemStack, ItemStack> comparison) {
        ItemStack[] contents = inv.getContents();
        for (int i = 0; i < contents.length && amount > 0; i++) {
            if (!comparison.test(item, contents[i])) {
                continue;
            }
            if (amount >= contents[i].getAmount()) {
                amount -= contents[i].getAmount();
                contents[i] = null;
                if (amount == 0) {
                    inv.setContents(contents);
                    return true;
                }
                continue;
            }
            contents[i].setAmount(contents[i].getAmount() - amount);
            inv.setContents(contents);
            return true;
        }
        inv.setContents(contents);
        return false;
    }

    /**
     * Removes the specified amount of the given item from the given inventory
     *
     * @param inv    The inventory to remove the items from
     * @param item   The item to be removed
     * @param amount The amount of items to remove
     * @return Whether the amount specified could be removed. False if it removed less than specified.
     */
    public static boolean remove(Inventory inv, ItemStack item, int amount) {
        return remove(inv, item, amount, ItemStack::isSimilar);
    }

    /**
     * Removes the specified amount of the given item type from the given inventory
     *
     * @param inv    The inventory to remove the items from
     * @param type   The item type to be removed
     * @param amount The amount of items to remove
     * @return Whether the amount specified could be removed. False if it removed less than specified.
     */
    public static boolean remove(Inventory inv, Material type, int amount) {
        return remove(inv, new ItemStack(type), amount, (a, b) -> compare(a, b, ItemTrait.TYPE));
    }

    /**
     * Remove all matching items up to a maximum, returning the number that were removed
     *
     * @param inv        The inventory to count and remove items from
     * @param item       The item to count and remove
     * @param max        The maximum number of items to remove
     * @param comparison A filter to compare items for counting and removal
     * @return How many items were removed
     */
    public static int countAndRemove(Inventory inv, ItemStack item, int max, BiPredicate<ItemStack, ItemStack> comparison) {
        int count = count(inv, item, comparison);
        count = Math.min(max, count);
        remove(inv, item, count, comparison);
        return count;
    }

    /**
     * Remove all matching items up to a maximum, returning the number that were removed
     *
     * @param inv  The inventory to count and remove items from
     * @param item The item to count and remove
     * @param max  The maximum number of items to remove
     * @return How many items were removed
     */
    public static int countAndRemove(Inventory inv, ItemStack item, int max) {
        return countAndRemove(inv, item, max, ItemStack::isSimilar);
    }

    /**
     * Remove all matching items up to a maximum, returning the number that were removed
     *
     * @param inv  The inventory to count and remove items from
     * @param type The item type to count and remove
     * @param max  The maximum number of items to remove
     * @return How many items were removed
     */
    public static int countAndRemove(Inventory inv, Material type, int max) {
        return countAndRemove(inv, new ItemStack(type), max, (a, b) -> compare(a, b, ItemTrait.TYPE));
    }

    /**
     * Remove all matching items, returning the number that were removed
     *
     * @param inv  The inventory to count and remove items from
     * @param item The item to count and remove
     * @return How many items were removed
     */
    public static int countAndRemove(Inventory inv, ItemStack item) {
        return countAndRemove(inv, item, Integer.MAX_VALUE, ItemStack::isSimilar);
    }

    /**
     * Remove all items of a specified type, returning the number that were removed
     *
     * @param inv  The inventory to count and remove items from
     * @param type The item type to count and remove
     * @return How many items were removed
     */
    public static int countAndRemove(Inventory inv, Material type) {
        return countAndRemove(inv, new ItemStack(type), Integer.MAX_VALUE, (a, b) -> ItemUtils.compare(a, b, ItemTrait.TYPE));
    }

    /**
     * Give the player the specified items, dropping them on the ground if there is not enough room
     *
     * @param player The player to give the items to
     * @param items  The items to be given
     */
    public static void give(Player player, ItemStack... items) {
        player.getInventory().addItem(items).values().forEach(i -> player.getWorld().dropItem(player.getLocation(), i));
    }

    /**
     * Gives the player the specified amount of the specified item, dropping them on the ground if there is not enough room
     *
     * @param player The player to give the items to
     * @param item   The item to be given to the player
     * @param amount The amount the player should be given
     */
    public static void give(Player player, ItemStack item, int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        int stackSize = item.getType().getMaxStackSize();
        while (amount > stackSize) {
            ItemStack clone = item.clone();
            clone.setAmount(stackSize);
            give(player, clone);
            amount -= stackSize;
        }
        ItemStack clone = item.clone();
        clone.setAmount(amount);
        give(player, clone);
    }

    /**
     * Gives the player the specified amount of the specified item type, dropping them on the ground if there is not enough room
     *
     * @param player The player to give the items to
     * @param type   The item type to be given to the player
     * @param amount The amount the player should be given
     */
    public static void give(Player player, Material type, int amount) {
        give(player, new ItemStack(type), amount);
    }

    public static ItemStack createSkull(String url) {
        ItemStack head = XMaterial.PLAYER_HEAD.parseItem();

        if (url.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwner("Notch");

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));

        Method setProfileMethod = null;
        try {
            setProfileMethod = headMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
        } catch (NoSuchMethodException | SecurityException ignored) {}
        try {
            // if available, we use setProfile(GameProfile) so that it sets both the profile field and the
            // serialized profile field for us. If the serialized profile field isn't set
            // ItemStack#isSimilar() and ItemStack#equals() throw an error.
            if (setProfileMethod == null) {
                Field profileField = headMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(headMeta, profile);
            } else {
                setProfileMethod.setAccessible(true);
                setProfileMethod.invoke(headMeta, profile);
            }
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | SecurityException | InvocationTargetException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

    /**
     * Compares the traits of two items
     *
     * @param first  The first ItemStack
     * @param second The second ItemStack
     * @param traits The ItemTraits to compare
     * @return Whether the two items are identical in terms of the traits provided. Returns true if both items are null, and false if only one is null.
     */
    public static boolean compare(ItemStack first, ItemStack second, ItemTrait... traits) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        for (ItemTrait trait : traits) {
            if (!trait.compare(first, second)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given item is null or AIR
     *
     * @param item item to check
     * @return Boolean with the result
     */
    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    /**
     * Translates all itemData from the recipient to the receiver
     *
     * @param recipient
     * @param receiver
     */
    public static void translateAllItemData(ItemStack recipient, ItemStack receiver) {
        try {
            receiver.setData(recipient.getData());
            receiver.setType(recipient.getType());
            receiver.setItemMeta(recipient.getItemMeta());
            receiver.setAmount(recipient.getAmount());
            receiver.setDurability(recipient.getDurability());
        } catch (IllegalArgumentException ignored) {
        }
    }

    public static <T> T getMetadata(ItemStack item, String metadata, Class<T> clazz) {
        return clazz.cast(new NBTItem(item).getObject(metadata, clazz));
    }

    public static ItemStack setMetadata(ItemStack item, String metadata, Object o) {
        NBTItem nbtItemize = new NBTItem(item);
        nbtItemize.setObject(metadata, o);
        return nbtItemize.getItem();
    }

    /**
     * Compares the type, name, and lore of two items
     *
     * @param first  The first ItemStack
     * @param second The second ItemStack
     * @return Whether the two items are identical in terms of type, name, and lore. Returns true if both items are null, and false if only one is null.
     */
    public static boolean compare(ItemStack first, ItemStack second) {
        return compare(first, second, ItemTrait.TYPE, ItemTrait.NAME, ItemTrait.LORE);
    }

    public static String serialize(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);
            dataOutput.close();

            return Base64Coder.encodeLines(outputStream.toByteArray());

        } catch (IOException e) {
            throw new IllegalStateException("Unable to serialize item.", e);
        }
    }

    public static ItemStack deserialize(String base64) {
        try {
            ByteArrayInputStream InputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(InputStream);

            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();

            return item;

        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize item.", e);
        }
    }


}
