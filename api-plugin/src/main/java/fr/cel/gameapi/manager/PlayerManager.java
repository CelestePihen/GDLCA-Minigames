package fr.cel.gameapi.manager;

import fr.cel.gameapi.manager.database.PlayerData;
import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class PlayerManager {

    private final Map<UUID, PlayerData> playersData = new HashMap<>();
    private final Set<UUID> playersInHub = new HashSet<>();

    @Setter private UUID newPlayer = null;
    private final Set<UUID> playersWhoWelcomed = new HashSet<>();

    private final Location spawnLocation = new Location(Bukkit.getWorld("world"), 264.5, 68, 90.5, 180F, 0F);

    /**
     * Adds the PlayerData of a player to the map
     * @param player The player who joined
     */
    public void addPlayerData(Player player) {
        playersData.put(player.getUniqueId(), new PlayerData(player));
    }

    /**
     * Removes the PlayerData of a player from the map
     * @param player The player who left
     */
    public void removePlayerData(Player player) {
        playersData.remove(player.getUniqueId());
    }

    /**
     * Gets the PlayerData of a player
     * @param player The player
     * @return Returns the PlayerData of the player
     */
    public PlayerData getPlayerData(Player player) {
        return playersData.get(player.getUniqueId());
    }

    // TODO: make an Event
    /**
     * Sends a player to the Hub
     * @param player The player to send
     */
    public void sendPlayerToHub(Player player) {
        playersInHub.add(player.getUniqueId());

        player.setGlowing(false);

        player.teleport(spawnLocation);
        player.setRespawnLocation(spawnLocation, true);
        player.setGameMode(GameMode.ADVENTURE);

        player.setFoodLevel(20);
        player.setExhaustion(20);
        player.setHealthScale(20);
        player.setHealth(20);

        player.setLevel(0);
        player.setExp(0);
        player.setTotalExperience(0);

        player.getInventory().clear();
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        player.getInventory().setItem(4, new ItemBuilder(Material.COMPASS)
                .displayName(Component.text("Sélectionneur de mini-jeux", NamedTextColor.WHITE))
                .toItemStack());

        player.getInventory().setItem(8, new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(player.getPlayerProfile())
                .displayName(Component.text("Mon Profil", NamedTextColor.WHITE))
                .toItemStack());
    }

    /**
     * Removes a player from the Hub players set
     * @param player The player to remove
     */
    public void removePlayerInHub(Player player) {
        playersInHub.remove(player.getUniqueId());
    }

    /**
     * Checks if a player is currently in the Hub
     * @param player The player to check
     * @return Returns true if the player is in the Hub, false otherwise
     */
    public boolean containsPlayerInHub(Player player) {
        return playersInHub.contains(player.getUniqueId());
    }

}