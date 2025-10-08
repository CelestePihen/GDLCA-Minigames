package fr.cel.gameapi.utils.zone;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ZoneDetection implements Listener, IZone {

    private final Location minCorner, maxCorner;
    private final JavaPlugin main;

    @Getter private final Map<UUID, Boolean> playerInside = new HashMap<>();

    private List<UUID> playersInGame;

    /**
     * Player detection zone: detects whether a player is inside the defined area.
     * @param minCorner The first corner of the zone
     * @param maxCorner The opposite corner of the zone
     * @param main The plugin instance
     */
    public ZoneDetection(Location minCorner, Location maxCorner, JavaPlugin main) {
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
        this.main = main;
    }

    /**
     * Activates the detection event for all players in the list.
     */
    @Override
    public void startChecking(List<UUID> playersInGame) {
        this.playersInGame = new ArrayList<>(playersInGame);
        for (UUID uuid : this.playersInGame) playerInside.putIfAbsent(uuid, false);
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    /**
     * Stops checking the zone and clears tracking data.
     */
    @Override
    public void stopChecking() {
        HandlerList.unregisterAll(this);
        playerInside.clear();
        playersInGame.clear();
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (this.playersInGame.contains(uuid)) playerInside.put(uuid, ZoneUtils.isInZone(player.getLocation(), minCorner, maxCorner));
    }

}