package io.github.divios.core_lib.inventory;

import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class inventoryUtils {

    /**
     * Returns a copy of the Inventory passed,
     * same holder, same size, same title, same contents
     * @param inv inventory to be cloned
     * @return cloned inventory
     */
    public static Inventory cloneInventory(Inventory inv, String title) {

        Inventory cloned = Bukkit.createInventory(inv.getHolder(),
                inv.getSize(), title);

        cloned.setContents(inv.getContents());
        return cloned;
    }

    /**
     * Translates all the items from one inventory to another. This method
     * is aware of the inventory sizes and will not throw errors
     * @param from
     * @param to
     */
    public static void translateContents(Inventory from, Inventory to) {
        if (from.getSize() > to.getSize())
            to.setContents(Arrays.stream(from.getContents()).limit(to.getSize()).toArray(ItemStack[]::new));
        else
            to.setContents(from.getContents());
    }

    /**
     * Gets the first empty slot of an item
     * @param inv
     * @return
     */
    public static int getFirstEmpty(Inventory inv) {
        int slot = -1;

        for (int i=0; i < inv.getSize(); i++) {
            if (ItemUtils.isEmpty(inv.getItem(i))) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getEmptySlots(Inventory inv) {
        return Math.toIntExact(Arrays.stream(inv.getContents())
                .filter(ItemUtils::isEmpty)
                .count());
    }

    /**
     * Serializes an inventory to base64
     * @param inv to be serialized
     * @return base64 serialized inventory
     */
    public static String serialize(Inventory inv, String title) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Save inventory's size
            dataOutput.writeInt(inv.getSize());

            // Save inventory's title
            dataOutput.writeObject(title);

            // Save every item in the inventory
            for (ItemStack item: inv.getContents())
                dataOutput.writeObject(item == null ?
                        new ItemStack(Material.AIR) : item);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());

        } catch (IOException e) {
            throw new IllegalStateException("Unable to serialize inventory.", e);
        }
    }

    /**
     * Deserialize an item from base64. You can specify a holder for this new inventory
     * @param base64 string on base 64 to deserialize
     * @param holder holder to set for the new inventory
     * @return deserialized inventory
     */
    public static Pair<String, Inventory> deserialize(String base64, @Nullable InventoryHolder holder) {
        try {
            Inventory inv;
            ByteArrayInputStream InputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(InputStream);

            // Get title and size
            final int size = dataInput.readInt();
            final String title = (String) dataInput.readObject();

            // Create inventory
            inv = Bukkit.createInventory(holder, size, title);

            // Get back all the items in the inventory
            for (int i = 0; i < size; i++) {
                ItemStack item = (ItemStack) dataInput.readObject();
                inv.setItem(i, item == null ? new ItemStack(Material.AIR):item);
            }

            dataInput.close();
            return Pair.of(title, inv);

        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize inventory.", e);
        }
    }

    /**
     * Deserializes an inventory from base with it's holder to null. Check
     * {@link inventoryUtils#deserialize(String, InventoryHolder)} for more info
     * @param base64 string on base 64 to deserialize
     * @return deserialized inventory
     */
    public static Pair<String, Inventory> deserialize(String base64) {
        return deserialize(base64, null);
    }




}
