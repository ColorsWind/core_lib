package io.github.divios.core_lib.inventory;

import io.github.divios.core_lib.itemutils.ItemUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.util.function.Consumer;

/**
 *
 * @author Redempt
 *
 */
public class ItemButton implements Serializable{

    protected ItemStack item;
    private int slot;
    private final SerializableConsumer<InventoryClickEvent> listener;

    /**
     * Create an ItemButton from the given ItemStack and listener.
     * Useful if you, like most people, would rather use lambdas than the anonymous class definition.
     * @param item The ItemStack to be used as this button's icon
     * @param listener The listener which will be called whenever this button is clicked
     * @return The ItemButton, which can be added to an InventoryGUI
     */
    public static ItemButton create(ItemStack item, SerializableConsumer<InventoryClickEvent> listener) {
        return new ItemButton(item, listener);
    }

    /**
     * Create a new ItemButton with the given ItemStack as the icon
     * @param item The ItemStack to be used as the icon
     */
    public ItemButton(ItemStack item, SerializableConsumer<InventoryClickEvent> listener) {
        this.item = item;
        this.listener = listener;
    }

    /**
     * Get the ItemStack representing the icon for this button
     * @return The ItemStack
     */
    public ItemStack getItem() {
        return item;
    }

    protected int getSlot() {
        return slot;
    }

    protected void setSlot(int slot) {
        this.slot = slot;
    }

    /**
     * Update the item of this button. Does not refresh the InventoryGUI; you must call {@link InventoryGUI#update()} for this change to be reflected in the GUI.
     * @param item The item to become the icon for this button
     */
    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void onClick(InventoryClickEvent e) {
        listener.accept(e);
    }

    public interface SerializableConsumer<T> extends Consumer<T>, Serializable {}

    public String serialize() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputData = new ObjectOutputStream(outputStream);

            outputData.writeObject(ItemUtils.serialize(item));
            outputData.writeObject(listener);
            outputData.writeInt(slot);

            outputData.close();

            return Base64Coder.encodeLines(outputStream.toByteArray());

        } catch (IOException e) {
            throw new IllegalStateException("Unable to serialize ItemButton.", e);
        }
    }

    public static ItemButton deserialize(String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            ObjectInputStream dataInput = new ObjectInputStream(inputStream);

            ItemButton button = ItemButton.create(ItemUtils.deserialize((String) dataInput.readObject()),
                    (SerializableConsumer<InventoryClickEvent>) dataInput.readObject());

            button.setSlot(dataInput.readInt());

            return button;

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to deserialize ItemButton.", e);
        }
    }


}