/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package io.github.divios.core_lib.metadata.type;

import io.github.divios.core_lib.metadata.MetadataKey;
import io.github.divios.core_lib.metadata.MetadataMap;
import io.github.divios.core_lib.metadata.MetadataRegistry;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A registry which provides and stores {@link MetadataMap}s for {@link World}s.
 */
public interface WorldMetadataRegistry extends MetadataRegistry<UUID> {

    /**
     * Produces a {@link MetadataMap} for the given world.
     *
     * @param world the world
     * @return a metadata map
     */
    @Nonnull
    MetadataMap provide(@Nonnull World world);

    /**
     * Gets a {@link MetadataMap} for the given world, if one already exists and has
     * been cached in this registry.
     *
     * @param world the world
     * @return a metadata map, if present
     */
    @Nonnull
    Optional<MetadataMap> get(@Nonnull World world);

    /**
     * Gets a map of the worlds with a given metadata key
     *
     * @param key the key
     * @param <K> the key type
     * @return an immutable map of worlds to key value
     */
    @Nonnull
    <K> Map<World, K> getAllWithKey(@Nonnull MetadataKey<K> key);

}
