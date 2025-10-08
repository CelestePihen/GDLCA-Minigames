package fr.cel.gameapi.utils.zone;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ZonePassage implements Listener, IZone {

    private final Location minCorner, maxCorner;
    private final JavaPlugin main;

    @Getter private final List<UUID> playersInGamePassed = new ArrayList<>();

    private List<UUID> playersInGame;

    /**
     * Passage detection zone: detects whether a player has entered the area.
     * @param minCorner The first corner of the zone
     * @param maxCorner The opposite corner of the zone
     * @param main The plugin instance
     */
    public ZonePassage(Location minCorner, Location maxCorner, JavaPlugin main) {
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
        this.main = main;
    }

    /**
     * Starts checking players entering the zone.
     */
    public void startChecking(List<UUID> playersInGame) {
        this.playersInGame = new ArrayList<>(playersInGame);
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    /**
     * Stops checking the zone and clears tracking data.
     */
    public void stopChecking() {
        HandlerList.unregisterAll(this);
        playersInGame.clear();
        playersInGamePassed.clear();
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (!this.playersInGame.contains(uuid)) return;
        if (ZoneUtils.isInZone(player.getLocation(), minCorner, maxCorner) && !playersInGamePassed.contains(uuid))
            playersInGamePassed.add(uuid);
    }

}