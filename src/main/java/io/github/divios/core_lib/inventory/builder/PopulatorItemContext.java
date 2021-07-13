package io.github.divios.core_lib.inventory.builder;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface PopulatorItemContext {

    PopulatorContentContext ofGlass();

    PopulatorContentContext of(List<ItemStack> toFill);

}
