package fr.cel.gameapi.utils.zone;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ZoneStay implements IZone {

    private final JavaPlugin main;
    private final Location minCorner, maxCorner;

    @Getter private final Map<UUID, Integer> playersInZone = new HashMap<>();

    private List<UUID> playersInGame;
    private BukkitRunnable runnable;

    /**
     * Stay zone: tracks how many seconds a player stays inside the area.
     * @param minCorner The first corner of the zone
     * @param maxCorner The opposite corner of the zone
     * @param main The plugin instance
     */
    public ZoneStay(Location minCorner, Location maxCorner, JavaPlugin main) {
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
        this.main = main;
    }

    /**
     * Starts checking players and increments their time in the zone every second.
     */
    @Override
    public void startChecking(List<UUID> playersInGame) {
        this.playersInGame = new ArrayList<>(playersInGame);

        for (UUID uuid : this.playersInGame) {
            playersInZone.putIfAbsent(uuid, 0);
        }

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : ZoneStay.this.playersInGame) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) continue;
                    if (ZoneUtils.isInZone(player.getLocation(), minCorner, maxCorner))
                        playersInZone.put(uuid, playersInZone.get(uuid) + 1);
                }
            }
        };

        runnable.runTaskTimer(main, 0L, 20L);
    }

    /**
     * Stops checking the zone and clears tracking data.
     */
    public void stopChecking() {
        if (runnable != null) runnable.cancel();
        if (playersInGame != null) playersInGame.clear();
        playersInZone.clear();
    }

}