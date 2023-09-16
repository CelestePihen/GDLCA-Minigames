package fr.cel.hub.manager;

import fr.cel.hub.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PlayerManager {

    private final List<UUID> playersInHub;
    private final List<UUID> playersInInstitution;
    
    private final Location spawnLocation;
    private final Location institutionLocation;

    public PlayerManager() {
        playersInHub = new ArrayList<>();
        playersInInstitution = new ArrayList<>();

        spawnLocation = new Location(Bukkit.getWorld("world"), 264.5, 68, 90.5, 180F, 0F);
        institutionLocation = new Location(Bukkit.getWorld("institution"), 264.5, 68, 90.5, 180F, 0F);
    }

    /*
     * Monde Normal
    */


    /**
     * Ajoute le joueur dans la liste des joueurs dans le Hub
     * @param player Le joueur à ajouter
     */
    private void addPlayerInHub(Player player) {
        if (containsPlayerInHub(player)) return;
        playersInHub.add(player.getUniqueId());
    }

    /**
     * Retire le joueur dans la liste des joueurs dans le Monde Normal
     * @param player Le joueur à retirer
     */
    public void removePlayerInHub(Player player) {
        if (!containsPlayerInHub(player)) return;
        
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

    /**
     * Permet d'envoyer le joueur dans le Hub
     * @param player Le joueur à envoyer
     */
    public void sendPlayerToHub(Player player) {
        removePlayerInInstitution(player);
        addPlayerInHub(player);

        player.setGlowing(false);
        player.teleport(spawnLocation);
        player.setBedSpawnLocation(spawnLocation, true);
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.setHealthScale(20);
        player.setHealth(20);
        player.setLevel(0);
        player.getInventory().clear();
        player.getInventory().setItem(4, new ItemBuilder(Material.COMPASS).setDisplayName("&rSélectionneur de mini-jeux").toItemStack());
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }

    /*
     * Monde Institution
    */

    /**
     * Ajoute le joueur dans la liste des joueurs dans l'Institution
     * @param player Le joueur à ajouter
     */
    private void addPlayerInInstitution(Player player) {
        if (containsPlayerInInstitution(player)) return;
        playersInInstitution.add(player.getUniqueId());
    }

    /**
     * // Retire le joueur dans la liste des joueurs dans l'Institution
     * @param player Le joueur à retirer
     */
    public void removePlayerInInstitution(Player player) {
        if (!containsPlayerInInstitution(player)) return;

        playersInInstitution.remove(player.getUniqueId());
    }

    /**
     * Détecte si le joueur est dans l'Institution
     * @param player Le joueur à détecter
     * @return Renvoie si le joueur est dans l'Institution ou pas
     */
    public boolean containsPlayerInInstitution(Player player) {
        return playersInInstitution.contains(player.getUniqueId());
    }

    /**
     * Permet d'envoyer le joueur dans l'Institution
     * @param player Le joueur à envoyer
     */
    public void sendPlayerToInstitution(Player player) {
        removePlayerInHub(player);
        addPlayerInInstitution(player);

        player.setGlowing(false);
        player.teleport(institutionLocation);
        player.setBedSpawnLocation(institutionLocation, true);
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.setHealthScale(20);
        player.setHealth(20);
        player.setLevel(0);
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }

}