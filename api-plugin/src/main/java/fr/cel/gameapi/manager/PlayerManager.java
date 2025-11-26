package fr.cel.gameapi.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.PlayerData;
import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
public class PlayerManager {

    private final Map<UUID, PlayerData> playersData = new HashMap<>();
    private final Set<UUID> playersInHub = new HashSet<>();

    @Setter private UUID newPlayer = null;
    private final Set<UUID> playersWhoWelcomed = new HashSet<>();

    // TODO: move to config
    private final Location spawnLocation = new Location(Bukkit.getWorld("world"), 264.5, 68, 90.5, 180F, 0F);

    /**
     * Adds the PlayerData of a player to the map
     * @param player The player who joined
     */
    public void addPlayerData(@NotNull Player player) {
        playersData.put(player.getUniqueId(), new PlayerData(player));
    }

    /**
     * Removes the PlayerData of a player from the map
     * @param player The player who left
     */
    public void removePlayerData(@NotNull Player player) {
        playersData.remove(player.getUniqueId());
    }

    /**
     * Gets the PlayerData of a player
     * @param player The player
     * @return Returns the PlayerData of the player
     */
    public PlayerData getPlayerData(@NotNull Player player) {
        return playersData.get(player.getUniqueId());
    }

    /**
     * Gets the PlayerData of a player
     * @param uuid The UUID of the player
     * @return Returns the PlayerData of the player
     */
    public PlayerData getPlayerData(UUID uuid) {
        return playersData.get(uuid);
    }

    // TODO: call a PlayerSendToHubEvent
    /**
     * Sends a player to the Hub
     * @param player The player to send
     */
    public void sendPlayerToHub(@NotNull Player player) {
        playersInHub.add(player.getUniqueId());

        // Utility
        player.teleportAsync(spawnLocation);
        player.setRespawnLocation(spawnLocation, true);
        player.setGameMode(GameMode.ADVENTURE);

        // Reset health, food
        player.setFoodLevel(20);
        player.setExhaustion(20);
        player.setHealthScale(20);
        player.setHealth(20);

        // Reset XP
        player.setLevel(0);
        player.setExp(0);
        player.setTotalExperience(0);

        // Clear effects and give hunger effect to hide food bar
        player.setGlowing(false);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 255, false, false, false));

        // Clear inventory and give hub items
        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(Material.ARMOR_STAND)
                .itemName(Component.text("Cosmétiques", NamedTextColor.WHITE))
                .toItemStack());

        player.getInventory().setItem(4, new ItemBuilder(Material.COMPASS)
                .itemName(Component.text("Sélectionneur de mini-jeux", NamedTextColor.WHITE))
                .toItemStack());

        player.getInventory().setItem(8, new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(player.getPlayerProfile())
                .customName(Component.text("Mon Profil", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .toItemStack());

        // Remove dressing cosmetics and reapply all cosmetics after a short delay
        if (GameAPI.getInstance().getCosmeticsManager().getDressingManager().isPlayerDressed(player))
            GameAPI.getInstance().getCosmeticsManager().getDressingManager().undressPlayer(player);

        Bukkit.getScheduler().runTaskLater(GameAPI.getInstance(),
                () -> GameAPI.getInstance().getCosmeticsManager().reapplyCosmetics(player), 5L);
    }

    /**
     * Removes a player from the Hub players set
     * @param player The player to remove
     */
    public void removePlayerInHub(@NotNull Player player) {
        playersInHub.remove(player.getUniqueId());
    }

    /**
     * Checks if a player is currently in the Hub
     * @param player The player to check
     * @return Returns true if the player is in the Hub, false otherwise
     */
    public boolean containsPlayerInHub(@NotNull Player player) {
        return playersInHub.contains(player.getUniqueId());
    }

}