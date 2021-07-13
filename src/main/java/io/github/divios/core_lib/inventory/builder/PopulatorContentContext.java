package io.github.divios.core_lib.inventory.builder;

import org.bukkit.inventory.Inventory;

public interface PopulatorContentContext {

    PopulatorContentContext mask(String mask);

    PopulatorContentContext scheme(int... scheme);

    inventoryPopulatorState toState();

    void apply(Inventory inv);

}
