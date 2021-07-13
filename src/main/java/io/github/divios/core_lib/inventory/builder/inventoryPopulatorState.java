package io.github.divios.core_lib.inventory.builder;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface inventoryPopulatorState {

    List<ItemStack> getToFill();

    List<List<Integer>> getMasks();

    List<List<Integer>> getSchemes();

    PopulatorContentContext restore();


}
