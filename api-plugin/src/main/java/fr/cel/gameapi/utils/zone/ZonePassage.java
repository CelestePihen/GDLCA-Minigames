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

    private List<UUID> playersInGame;
    @Getter private final List<UUID> playersInGamePassed = new ArrayList<>();

    /**
     * La Zone de Passage sert à détecter si un joueur est passé dans une zone
     * @param minCorner La position du 1er coin de la zone
     * @param maxCorner La position du 2ème coin de la zone
     * @param main L'instance  du plugin
     */
    public ZonePassage(Location minCorner, Location maxCorner, JavaPlugin main) {
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
        this.main = main;
    }

    /**
     * Vérifie en boucle si des joueurs sont dans la zone
     */
    public void startChecking(List<UUID> playersInGame) {
        this.playersInGame = new ArrayList<>(playersInGame);
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    /**
     * Arrête la vérification dans la zone
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
        if (isInZone(player.getLocation()) && !playersInGamePassed.contains(uuid)) playersInGamePassed.add(uuid);
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