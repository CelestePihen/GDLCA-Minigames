package fr.cel.gameapi.manager;

import fr.cel.gameapi.manager.database.PlayerData;
import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
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
     * Ajoute le PlayerData du joueur dans la Map
     * @param player Le joueur qui a rejoint
     */
    public void addPlayerData(Player player) {
        playersData.put(player.getUniqueId(), new PlayerData(player));
    }

    /**
     * Permet de retirer le PlayerData du joueur de la Map
     * @param player Le joueur qui a quitté
     */
    public void removePlayerData(Player player) {
        playersData.remove(player.getUniqueId());
    }

    /**
     * Permet d'obtenir le PlayerData du joueur
     * @param player Le joueur
     * @return Retourne le PlayerData
     */
    public PlayerData getPlayerData(Player player) {
        return playersData.get(player.getUniqueId());
    }

    /**
     * Permet d'envoyer le joueur au Hub
     * @param player Le joueur à envoyer
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
        player.getInventory().setItem(4, new ItemBuilder(Material.COMPASS).setDisplayName("&rSélectionneur de mini-jeux").toItemStack());
        player.getInventory().setItem(8, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(player.getPlayerProfile()).setDisplayName("&fMon Profil").toItemStack());
    }

    /**
     * Retire le joueur dans la liste des joueurs dans le Monde Normal
     * @param player Le joueur à retirer
     */
    public void removePlayerInHub(Player player) {
        playersInHub.remove(player.getUniqueId());
    }

    /**
     * Détecte si le joueur est dans le Hub
     * @param player Le joueur à détecter
     * @return Renvoie si le joueur est dans le Hub ou pas
     */
    public boolean containsPlayerInHub(Player player) {
        return playersInHub.contains(player.getUniqueId());
    }

}