package fr.cel.cachecache.map.timer.game;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.state.game.PlayingMapState;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GiftsMapTask extends BukkitRunnable {

    private static final ItemStack GIFT_ITEM;
    private static final PlayerProfile GIFT_PROFILE = Bukkit.createProfile(UUID.randomUUID());

    static {
        GIFT_PROFILE.setProperty(new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDhhYTJmOWJmZmQ4ODUzNjQ2YzEzNzVkM2RlNzFiODE4N2JkNDI2MzMyZDlmOWQ3MTIyMTZmN2U5MmE5MzBkIn19fQ=="));

        GIFT_ITEM = new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(GIFT_PROFILE)
                .customName(Component.text("Cadeau"))
                .toItemStack();
    }

    private final CCMap map;

    public GiftsMapTask(CCMap map) {
        this.map = map;
    }

    @Override
    public void run() {
        cancel();

        List<Location> farthestLocations = getFarthestLocationsFromPlayers();

        spawnGiftAt(farthestLocations.getFirst());
        spawnGiftAt(farthestLocations.get(1));
        map.sendMessage(Component.text("Deux cadeaux sont apparus !", NamedTextColor.GREEN));

        if (map.getMapState() instanceof PlayingMapState state) state.runNewGiftTask();
    }

    /**
     * Trouve les 2 locations les plus éloignées de tous les joueurs
     * @return Liste des 2 locations les plus éloignées
     */
    private List<Location> getFarthestLocationsFromPlayers() {
        List<Location> availableLocations = map.getWinterUtility().getGiftLocations();

        List<Location> playerLocations = new ArrayList<>();
        for (UUID uuid : map.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) playerLocations.add(player.getLocation());
        }

        Map<Location, Double> locationMinDistances = new HashMap<>();

        for (Location giftLoc : availableLocations) {
            double minDistance = Double.MAX_VALUE;

            for (Location playerLoc : playerLocations) {
                if (giftLoc.getWorld() != null && giftLoc.getWorld().equals(playerLoc.getWorld())) {
                    double distance = giftLoc.distance(playerLoc);
                    if (distance < minDistance) minDistance = distance;
                }
            }

            locationMinDistances.put(giftLoc, minDistance);
        }

        // Locations les plus éloignées en premier
        List<Map.Entry<Location, Double>> sortedLocations = new ArrayList<>(locationMinDistances.entrySet());
        sortedLocations.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Location> result = new ArrayList<>();
        result.add(sortedLocations.getFirst().getKey());
        result.add(sortedLocations.get(1).getKey());

        return result;
    }

    /**
     * Fait apparaître un cadeau à la location spécifiée
     * @param location La location où faire apparaître le cadeau
     */
    private void spawnGiftAt(Location location) {
        Item droppedItem = location.getWorld().dropItem(location, GIFT_ITEM);
        droppedItem.setUnlimitedLifetime(true);
        droppedItem.setGlowing(true);

        map.getSpawnedGroundItems().add(droppedItem.getUniqueId());
    }

}