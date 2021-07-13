package io.github.divios.core_lib.inventory.builder;

import io.github.divios.core_lib.inventory.ItemButton;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface paginatedGuiBuilder {

    paginatedGuiBuilder withTitle(String title);

    paginatedGuiBuilder withItems(List<ItemButton> items);

    paginatedGuiBuilder withItems(Stream<ItemButton> items);

    paginatedGuiBuilder withBackButton(ItemStack item, int slot);

    paginatedGuiBuilder withNextButton(ItemStack item, int slot);

    paginatedGuiBuilder withPopulator(inventoryPopulatorState populator);

    paginatedGuiBuilder withPopulator(PopulatorContentContext populator);

    paginatedGui build();
}
