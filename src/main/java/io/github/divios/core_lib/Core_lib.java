package io.github.divios.core_lib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core_lib extends JavaPlugin {

    private static Core_lib PLUGIN;

    public static final int MID_VERSION = Integer.parseInt(getServerVersion().split("\\.")[1]);

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static String getServerVersion() {
        String version = Bukkit.getVersion();
        String[] split = version.split(" ");
        return split[split.length - 1].trim().replace(")", "");
    }

}
