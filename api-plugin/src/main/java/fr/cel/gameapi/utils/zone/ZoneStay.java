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

    private List<UUID> playersInGame;
    @Getter private final Map<UUID, Integer> playersInZone = new HashMap<>();

    private BukkitRunnable runnable;

    /**
     * Cette zone sert à détecter combien de secondes un joueur est resté dans celle-ci
     * @param minCorner La position du 1er coin de la zone
     * @param maxCorner La position du 2ème coin de la zone
     * @param main L'instance du plugin
     */
    public ZoneStay(Location minCorner, Location maxCorner, JavaPlugin main) {
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
        this.main = main;
    }

    /**
     * Vérifie en boucle si des joueurs sont dans la zone
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
                    if (isInZone(player.getLocation())) playersInZone.put(uuid, playersInZone.get(uuid) + 1);
                }
            }
        };

        runnable.runTaskTimer(main, 0L, 20L);
    }

    /**
     * Arrête la vérification dans la zone
     */
    public void stopChecking() {
        if (runnable != null) runnable.cancel();
        if (playersInGame != null) playersInGame.clear();
        playersInZone.clear();
    }

    /**
     * Vérifie si une position (celle du joueur, un bloc, etc) est dans une zone
     * @see org.bukkit.Location
     */
    private boolean isInZone(Location loc) {
        double minX = Math.min(minCorner.getX(), maxCorner.getX());
        double maxX = Math.max(minCorner.getX(), maxCorner.getX());
        double minY = Math.min(minCorner.getY(), maxCorner.getY());
        double maxY = Math.max(minCorner.getY(), maxCorner.getY());
        double minZ = Math.min(minCorner.getZ(), maxCorner.getZ());
        double maxZ = Math.max(minCorner.getZ(), maxCorner.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX
                && loc.getY() >= minY && loc.getY() <= maxY
                && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

}