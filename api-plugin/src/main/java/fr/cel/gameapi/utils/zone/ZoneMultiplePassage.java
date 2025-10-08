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

public class ZoneMultiplePassage implements Listener, IZone {

    private final Location minCorner, maxCorner;
    private final JavaPlugin main;

    @Getter private final Map<UUID, Integer> playersPassCount = new HashMap<>();
    private final Map<UUID, Boolean> playerInside = new HashMap<>();

    private List<UUID> playersInGame;

    /**
     * Passage zone: counts how many times a player passes through the defined area.
     * @param minCorner The first corner of the zone
     * @param maxCorner The opposite corner of the zone
     * @param main The plugin instance
     */
    public ZoneMultiplePassage(Location minCorner, Location maxCorner, JavaPlugin main) {
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
        this.main = main;
    }

    /**
     * Starts checking player passages.
     * Initializes pass count and registers event listeners.
     */
    @Override
    public void startChecking(List<UUID> playersInGame) {
        this.playersInGame = new ArrayList<>(playersInGame);

        for (UUID uuid : playersInGame) {
            playerInside.putIfAbsent(uuid, false);
            playersPassCount.putIfAbsent(uuid, 0);
        }

        main.getServer().getPluginManager().registerEvents(this, main);
    }

    /**
     * Stops checking the zone and clears all tracking data.
     */
    public void stopChecking() {
        HandlerList.unregisterAll(this);
        playerInside.clear();
        playersPassCount.clear();
        playersInGame.clear();
    }

    /**
     * Handles player movement to detect passages.
     * Increments the pass count when a player enters the zone.
     */
    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!playersInGame.contains(uuid)) return;
        if (event.getFrom().getYaw() != event.getTo().getYaw() || event.getFrom().getPitch() != event.getTo().getPitch()) return;

        boolean isPlayerIn = ZoneUtils.isInZone(player.getLocation(), minCorner, maxCorner);

        if (isPlayerIn && !playerInside.get(uuid)) {
            playersPassCount.put(uuid, playersPassCount.get(uuid) + 1);
            playerInside.put(uuid, true);
        } else if (!isPlayerIn && playerInside.get(uuid)) {
            playerInside.put(uuid, false);
        }
    }

}