package io.github.divios.core_lib.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.core_lib.Core_lib;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * An immutable and serializable block location object
 */
public final class BlockPosition{
    public static BlockPosition deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("x"));
        Preconditions.checkArgument(object.has("y"));
        Preconditions.checkArgument(object.has("z"));
        Preconditions.checkArgument(object.has("world"));

        int x = object.get("x").getAsInt();
        int y = object.get("y").getAsInt();
        int z = object.get("z").getAsInt();
        String world = object.get("world").getAsString();

        return of(x, y, z, world);
    }

    public static BlockPosition of(int x, int y, int z, String world) {
        Objects.requireNonNull(world, "world");
        return new BlockPosition(x, y, z, world);
    }

    public static BlockPosition of(int x, int y, int z, World world) {
        Objects.requireNonNull(world, "world");
        return of(x, y, z, world.getName());
    }

    public static BlockPosition of(Location location) {
        Objects.requireNonNull(location, "location");
        return of(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
    }

    public static BlockPosition of(Block block) {
        Objects.requireNonNull(block, "block");
        return of(block.getLocation());
    }

    private final int x;
    private final int y;
    private final int z;
    private final String world;

    @Nullable
    private Location bukkitLocation = null;

    private BlockPosition(int x, int y, int z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public String getWorld() {
        return this.world;
    }

    public synchronized Location toLocation() {
        if (this.bukkitLocation == null) {
            this.bukkitLocation = new Location(Core_lib.getPlugin().getServer().getWorld(this.world), this.x, this.y, this.z);
        }

        return this.bukkitLocation.clone();
    }

    public Block toBlock() {
        return toLocation().getBlock();
    }

    public BlockPosition getRelative(BlockFace face) {
        Objects.requireNonNull(face, "face");
        return BlockPosition.of(this.x + face.getModX(), this.y + face.getModY(), this.z + face.getModZ(), this.world);
    }

    public BlockPosition getRelative(BlockFace face, int distance) {
        Objects.requireNonNull(face, "face");
        return BlockPosition.of(this.x + (face.getModX() * distance), this.y + (face.getModY() * distance), this.z + (face.getModZ() * distance), this.world);
    }

    public BlockPosition add(int x, int y, int z) {
        return BlockPosition.of(this.x + x, this.y + y, this.z + z, this.world);
    }


    public BlockPosition subtract(int x, int y, int z) {
        return add(-x, -y, -z);
    }

    public BlockRegion regionWith(BlockPosition other) {
        Objects.requireNonNull(other, "other");
        return BlockRegion.of(this, other);
    }


    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getX();
        result = result * PRIME + this.getY();
        result = result * PRIME + this.getZ();
        result = result * PRIME + this.getWorld().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BlockPosition(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", world=" + this.getWorld() + ")";
    }

}