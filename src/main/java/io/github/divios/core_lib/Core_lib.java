package io.github.divios.core_lib;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core_lib extends JavaPlugin {

    private static Core_lib PLUGIN;

    @Override
    public void onEnable() {
        PLUGIN = this;

        int pluginId = 11553;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Core_lib getInstance() {
        return PLUGIN;
    }

}
