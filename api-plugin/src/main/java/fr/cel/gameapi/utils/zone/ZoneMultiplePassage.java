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

    private List<UUID> playersInGame;

    @Getter private final Map<UUID, Integer> playersPassCount = new HashMap<>();
    private final Map<UUID, Boolean> playerInside = new HashMap<>();

    /**
     * La Zone de Passage sert à compter combien de fois un joueur est passé dedans
     * @param minCorner La position du 1er coin de la zone
     * @param maxCorner La position du 2ème coin de la zone
     * @param main L'instance  du plugin
     */
    public ZoneMultiplePassage(Location minCorner, Location maxCorner, JavaPlugin main) {
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
        this.main = main;
    }

    /**
     * Vérifie en boucle si des joueurs sont dans la zone
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!this.playersInGame.contains(uuid)) return;
        if (event.getFrom().getYaw() != event.getTo().getYaw() || event.getFrom().getPitch() != event.getTo().getPitch()) return;

        boolean isPlayerIn = isInZone(player.getLocation());

        if (isPlayerIn && !playerInside.get(uuid)) {
            playersPassCount.put(uuid, playersPassCount.get(uuid) + 1);
            playerInside.put(uuid, true);
        }
        else if (!isPlayerIn && playerInside.get(uuid)) {
            playerInside.put(uuid, false);
        }
    }

    @Override
    public void startChecking(List<UUID> playersInGame) {
        this.playersInGame = new ArrayList<>(playersInGame);

        for (UUID uuid : this.playersInGame) {
            playerInside.putIfAbsent(uuid, false);
            playersPassCount.putIfAbsent(uuid, 0);
        }

        main.getServer().getPluginManager().registerEvents(this, main);
    }

    /**
     * Arrête la vérification dans la zone
     */
    public void stopChecking() {
        HandlerList.unregisterAll(this);
        playerInside.clear();
        playersPassCount.clear();
        playersInGame.clear();
    }

    /**
     * Vérifie si une position (celle du joueur, un bloc, etc) est dans une zone
     * @see Location
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