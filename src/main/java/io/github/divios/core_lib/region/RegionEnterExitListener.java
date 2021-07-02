package io.github.divios.core_lib.region;

import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.region.events.RegionEnterEvent;
import io.github.divios.core_lib.region.events.RegionExitEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;


/**
 * @author Redempt
 */
public class RegionEnterExitListener {

    private static RegionMap<Region> regionMap = new RegionMap<>();
    private static boolean registered = false;

    /**
     * Registers the listener which calls {@link RegionEnterEvent} and {@link RegionExitEvent}. Called automatically
     * by RedLib upon enabling.
     */
    public static void register(Plugin plugin) {
        if (registered) {
            return;
        }
        registered = true;
        new EventListener<>(plugin, PlayerMoveEvent.class, e -> {
            regionMap.get(e.getFrom()).forEach(r -> {
                if (r.contains(e.getFrom()) && !r.contains(e.getTo())) {
                    Bukkit.getPluginManager().callEvent(new RegionExitEvent(e.getPlayer(), r, RegionExitEvent.ExitCause.MOVE, e));
                }
            });
            regionMap.get(e.getTo()).forEach(r -> {
                if (!r.contains(e.getFrom()) && r.contains(e.getTo())) {
                    Bukkit.getPluginManager().callEvent(new RegionEnterEvent(e.getPlayer(), r, RegionEnterEvent.EnterCause.MOVE, e));
                }
            });
        });
        new EventListener<>(plugin, PlayerTeleportEvent.class, e -> {
            regionMap.get(e.getFrom()).forEach(r -> {
                if (r.contains(e.getFrom()) && !r.contains(e.getTo())) {
                    Bukkit.getPluginManager().callEvent(new RegionExitEvent(e.getPlayer(), r, RegionExitEvent.ExitCause.TELEPORT, e));
                }
            });
            regionMap.get(e.getTo()).forEach(r -> {
                if (!r.contains(e.getFrom()) && r.contains(e.getTo())) {
                    Bukkit.getPluginManager().callEvent(new RegionEnterEvent(e.getPlayer(), r, RegionEnterEvent.EnterCause.TELEPORT, e));
                }
            });
        });
        new EventListener<>(plugin, PlayerQuitEvent.class, e -> {
            regionMap.get(e.getPlayer().getLocation()).forEach(r -> {
                if (r.contains(e.getPlayer().getLocation())) {
                    Bukkit.getPluginManager().callEvent(new RegionExitEvent(e.getPlayer(), r, RegionExitEvent.ExitCause.QUIT, null));
                }
            });
        });
        new EventListener<>(plugin, PlayerJoinEvent.class, e -> {
            regionMap.get(e.getPlayer().getLocation()).forEach(r -> {
                if (r.contains(e.getPlayer().getLocation())) {
                    Bukkit.getPluginManager().callEvent(new RegionEnterEvent(e.getPlayer(), r, RegionEnterEvent.EnterCause.JOIN, null));
                }
            });
        });
        new EventListener<>(plugin, PlayerDeathEvent.class, e -> {
            regionMap.get(e.getEntity().getLocation()).forEach(r -> {
                if (r.contains(e.getEntity().getLocation())) {
                    Bukkit.getPluginManager().callEvent(new RegionExitEvent(e.getEntity(), r, RegionExitEvent.ExitCause.DEATH, null));
                }
            });
        });
        new EventListener<>(plugin, PlayerRespawnEvent.class, e -> {
            regionMap.get(e.getPlayer().getLocation()).forEach(r -> {
                if (r.contains(e.getPlayer().getLocation())) {
                    Bukkit.getPluginManager().callEvent(new RegionEnterEvent(e.getPlayer(), r, RegionEnterEvent.EnterCause.RESPAWN, null));
                }
            });
        });
    }

    protected static RegionMap<Region> getRegionMap() {
        return regionMap;
    }

}