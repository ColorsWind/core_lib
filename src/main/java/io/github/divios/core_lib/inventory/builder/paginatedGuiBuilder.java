package io.github.divios.core_lib.inventory.builder;

import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface paginatedGuiBuilder {

    paginatedGuiBuilder withTitle(String title);

    paginatedGuiBuilder withTitle(BiFunction<Integer, Integer, String> title);

    default paginatedGuiBuilder withItems(Collection<ItemButton> items) {
        return withItems(items);
    }

    default paginatedGuiBuilder withItems(Stream<ItemButton> items) {
        return withItems(() -> items.collect(Collectors.toList()));
    }

    default paginatedGuiBuilder withItems(List<ItemButton> items) {
        return withItems(() -> items);
    }

    paginatedGuiBuilder withItems(Supplier<List<ItemButton>> items);

    paginatedGuiBuilder withBackButton(ItemStack item, int slot);

    paginatedGuiBuilder withNextButton(ItemStack item, int slot);

    paginatedGuiBuilder withExitButton(ItemButton item, int slot);

    paginatedGuiBuilder withExitButton(ItemStack item, Consumer<InventoryClickEvent> e, int slot);

    paginatedGuiBuilder withButtons(BiConsumer<InventoryGUI, Integer> withButtons);

    paginatedGuiBuilder withPopulator(inventoryPopulatorState populator);

    paginatedGuiBuilder withPopulator(PopulatorContentContext populator);

    paginatedGui build();

    CompletableFuture<paginatedGui> buildFuture();
}
