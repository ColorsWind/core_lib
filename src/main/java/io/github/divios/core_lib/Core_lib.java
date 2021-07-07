package io.github.divios.core_lib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core_lib extends JavaPlugin {

    private static Plugin PLUGIN;
    private static Thread mainThread = null;

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

    public static Plugin getPlugin() {
        return PLUGIN;
    }

    public static void setPlugin(Plugin plugin) { PLUGIN = plugin; }

    public static synchronized Thread getMainThread() {
        if (mainThread == null) {
            if (Bukkit.getServer().isPrimaryThread()) {
                mainThread = Thread.currentThread();
            }
        }
        return mainThread;
    }


}
