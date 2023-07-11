package fr.cel.hub.manager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    @Getter private List<UUID> playersInHub;
    @Getter private List<UUID> playersInInstitution;
    
    @Getter private final Location spawnLocation;
    @Getter private final Location institutionLocation;

    public PlayerManager() {
        playersInHub = new ArrayList<>();
        playersInInstitution = new ArrayList<>();

        spawnLocation = new Location(Bukkit.getWorld("world"), 264.5, 68, 90.5, 180F, 0F);
        institutionLocation = new Location(Bukkit.getWorld("institution"), 264.5, 68, 90.5, 180F, 0F);
    }

    /*
     * Monde Normal
    */

    // Ajoute le joueur dans la liste des joueurs dans le Monde Normal
    private void addPlayerInHub(Player player) {
        if (containsPlayerInHub(player)) return;
        playersInHub.add(player.getUniqueId());
    }

    // Retire le joueur dans la liste des joueurs dans le Monde Normal
    public void removePlayerInHub(Player player) {
        if (!containsPlayerInHub(player)) return;
        
        playersInHub.remove(player.getUniqueId());
    }

    public boolean containsPlayerInHub(Player player) {
        return playersInHub.contains(player.getUniqueId());
    }

    // Détecte si le joueur est dans le Monde Normal
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
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }

    /*
     * Monde Institution
    */

    // Ajoute le joueur dans la liste des joueurs dans l'Institution
    private void addPlayerInInstitution(Player player) {
        if (containsPlayerInInstitution(player)) return;
        playersInInstitution.add(player.getUniqueId());
    }

    // Retire le joueur dans la liste des joueurs dans l'Institution
    public void removePlayerInInstitution(Player player) {
        if (!containsPlayerInInstitution(player)) return;

        playersInInstitution.remove(player.getUniqueId());
    }

    // Détecte si le joueur est dans l'Institution
    public boolean containsPlayerInInstitution(Player player) {
        return playersInInstitution.contains(player.getUniqueId());
    }

    // Permet d'envoyer le joueur dans l'Institution
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
        player.getActivePotionEffects().forEach(potionEffect -> { player.removePotionEffect(potionEffect.getType()); });
    }

}