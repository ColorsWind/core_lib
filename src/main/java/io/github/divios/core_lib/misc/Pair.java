package io.github.divios.core_lib.misc;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.util.function.BiConsumer;

public class Pair<K, V> {

    private final K element0;
    private final V element1;

    public static <K, V> Pair<K, V> of(K element0, V element1) {
        return new Pair<>(element0, element1);
    }

    private Pair(K element0, V element1) {
        this.element0 = element0;
        this.element1 = element1;
    }

    public K get1() {
        return element0;
    }

    public V get2() {
        return element1;
    }

    public void stream(BiConsumer<K, V> stream) { stream.accept(get1(), get2()); }

    public String serialize() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream dataOutput = new ObjectOutputStream(outputStream);

            dataOutput.writeObject(get1());
            dataOutput.writeObject(get2());

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());

        } catch (IOException e) {
            throw new IllegalStateException("Unable to serialize Pair.", e);
        }
    }

    public static <K, V> Pair<K, V> deserialize(String base64, Class<K> class1, Class<V> class2) {
        try {
            ByteArrayInputStream InputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            ObjectInputStream dataInput = new ObjectInputStream(InputStream);

            Pair<K, V> newPair = new Pair<>(class1.cast(dataInput.readObject()),
                    class2.cast(dataInput.readObject()));

            dataInput.close();
            return newPair;

        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize Pair", e);
        }
    }

}