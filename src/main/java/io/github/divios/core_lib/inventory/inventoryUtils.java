package io.github.divios.core_lib.inventory;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.annotation.CheckForNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;


public class inventoryUtils {

    /**
     * Returns a copy of the Inventory passed,
     * same holder, same size, same title, same contents
     *
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
     *
     * @param from
     * @param to
     */
    public static void translateContents(Inventory from, Inventory to) {
        if (from.getSize() > to.getSize())
            to.setContents(Arrays.stream(from.getContents()).limit(to.getSize()).toArray(ItemStack[]::new));
        else
            to.setContents(from.getContents());
    }

    public static int playerEmptySlots(Player p) {
        return (int) IntStream.range(0, 36)
                .filter(value -> ItemUtils.isEmpty(p.getInventory().getItem(value)))
                .count();
    }

    /**
     * Gets the first empty slot of an item
     *
     * @param inv
     * @return
     */
    public static int getFirstEmpty(Inventory inv) {
        int slot = -1;

        for (int i = 0; i < inv.getSize(); i++) {
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
     *
     * @param inv to be serialized
     * @return base64 serialized inventory
     */
    public static String serialize(Inventory inv, String title) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                // Save inventory's size
                dataOutput.writeInt(inv.getSize());

                // Save inventory's title
                dataOutput.writeObject(title);

                // Save every item in the inventory
                for (ItemStack item : inv.getContents())
                    dataOutput.writeObject(item == null ?
                            new ItemStack(Material.AIR) : item);

                return Base64Coder.encodeLines(outputStream.toByteArray());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to serialize inventory.", e);
        }
    }

    /**
     * Deserialize an item from base64. You can specify a holder for this new inventory
     *
     * @param base64 string on base 64 to deserialize
     * @param holder holder to set for the new inventory
     * @return deserialized inventory
     */
    public static Pair<String, Inventory> deserialize(String base64, InventoryHolder holder) {
        Inventory inv;
        try (ByteArrayInputStream InputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64))) {
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(InputStream)) {
                // Get title and size
                final int size = dataInput.readInt();
                final String title = (String) dataInput.readObject();

                // Create inventory
                inv = Bukkit.createInventory(holder, size, title);

                // Get back all the items in the inventory
                for (int i = 0; i < size; i++) {
                    ItemStack item = (ItemStack) dataInput.readObject();
                    inv.setItem(i, item == null ? new ItemStack(Material.AIR) : item);
                }

                return Pair.of(title, inv);
            }

        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize inventory.", e);
        }
    }

    /**
     * Deserializes an inventory from base with it's holder to null. Check
     * {@link inventoryUtils#deserialize(String, InventoryHolder)} for more info
     *
     * @param base64 string on base 64 to deserialize
     * @return deserialized inventory
     */
    public static Pair<String, Inventory> deserialize(String base64) {
        return deserialize(base64, null);
    }

    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemAdapter())
            .create();

    private static final TypeToken<LinkedHashMap<Integer, ItemStack>> mapItemsToken = new TypeToken<LinkedHashMap<Integer, ItemStack>>() {
    };

    public static JsonElement toJson(String title, Inventory inv) {
        return JsonBuilder.object()
                .add("title", title)
                .add("items", gson.toJson(contentsToMap(inv.getContents())))
                .build();
    }

    private static Map<Integer, ItemStack> contentsToMap(ItemStack[] contents) {
        Map<Integer, ItemStack> mapContents = new LinkedHashMap<>();
        for (int i = 0; i < contents.length; i++) {
            mapContents.put(i, contents[i]);
        }
        return mapContents;
    }

    public static Inventory fromJson(JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        String title = object.get("title").getAsString();
        Map<Integer, ItemStack> mapContents = gson.fromJson(object.get("items"), mapItemsToken.getType());

        Inventory inv = Bukkit.createInventory(null, mapContents.size(), title);
        mapContents.forEach(inv::setItem);

        return inv;
    }

    private static final class ItemAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

        @Override
        public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonNull()) return null;

            NBTContainer compound = new NBTContainer(json.getAsString());
            return NBTItem.convertNBTtoItem(compound);
        }

        @Override
        public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) return JsonNull.INSTANCE;

            return new JsonPrimitive(NBTItem.convertItemtoNBT(src).toString());
        }
    }


}
