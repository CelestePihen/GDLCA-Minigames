package fr.cel.halloween.map.timer.game;

import fr.cel.halloween.map.HalloweenMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PlayingMapTask extends BukkitRunnable {

    private static final Random RANDOM = new Random();

    private final HalloweenMap map;

    public PlayingMapTask(HalloweenMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        if (map.getTimer() <= 0) {
            cancel();
            map.checkWinOrEndGame();
            return;
        }

        map.setTimer(map.getTimer() - 1);

        if (map.getTimer() == 870) {
            Player player = Bukkit.getPlayer(map.getTracker().getFirst());
            player.teleport(map.getSpawnLoc());

            map.sendMessage(Component.text("Le Traqueur ", NamedTextColor.RED).append(Component.text(player.getName() + " est libéré(e) !", NamedTextColor.WHITE)));
        }

        if (map.getTimer() % 120 == 0) {
            for (UUID uuid : map.getSouls()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;

                if (map.getPlayerDead().contains(player.getUniqueId())) {
                    map.getPlayerDead().remove(player.getUniqueId());

                    player.teleport(findSafeSpawnLocation(map));
                }
            }
            map.sendMessage(Component.text("Les Âmes errantes ", NamedTextColor.AQUA).append(Component.text("mortes sont réapparues !", NamedTextColor.WHITE)));
        }

        String timerString = String.format("%02dmin%02ds", (map.getTimer() % 3600) / 60, map.getTimer() % 60);

        for (UUID uuid : map.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) player.sendActionBar(Component.text(timerString));
        }

    }
    
    /**
     * Trouve un emplacement de spawn éloigné du traqueur
     */
    private Location findSafeSpawnLocation(HalloweenMap map) {
        List<Location> availableLocations = map.getPlayerSpawnsLocations();

        // Si pas de traqueur (pas censé arriver car fin de partie sinon) ou une seule location, retourner aléatoirement
        if (availableLocations.size() <= 1) {
            return availableLocations.get(RANDOM.nextInt(availableLocations.size()));
        }

        // Obtenir la position du traqueur
        Player tracker = Bukkit.getPlayer(map.getTracker().getFirst());
        // pas censé arriver car fin de partie sinon
        if (tracker == null) {
            return availableLocations.get(RANDOM.nextInt(availableLocations.size()));
        }

        Location trackerLoc = tracker.getLocation();

        // Trier les locations par distance au traqueur (du plus éloigné au plus proche)
        List<Location> sortedLocations = new ArrayList<>(availableLocations);
        sortedLocations.sort((loc1, loc2) -> {
            double dist1 = loc1.distanceSquared(trackerLoc);
            double dist2 = loc2.distanceSquared(trackerLoc);
            return Double.compare(dist2, dist1); // Ordre décroissant
        });

        // Choisir parmi les 3 spawns les plus éloignés
        int maxChoice = Math.min(3, sortedLocations.size());
        return sortedLocations.get(RANDOM.nextInt(maxChoice));
    }
}