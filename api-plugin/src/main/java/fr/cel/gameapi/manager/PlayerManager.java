package fr.cel.gameapi.manager;

import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.gameapi.utils.PlayerData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class PlayerManager extends ManagerAPI {

    private final Map<Player, PlayerData> playersDatas = new HashMap<>();

    private Set<UUID> playersInHub;
    
    private Location spawnLocation;

    public void enable() {
        playersInHub = new HashSet<>();
        spawnLocation = new Location(Bukkit.getWorld("world"), 264.5, 68, 90.5, 180F, 0F);
    }

    // BDD
    public void addPlayerData(Player player) {
        playersDatas.put(player, new PlayerData(player));
    }

    public PlayerData getPlayerData(Player player) {
        return playersDatas.get(player);
    }

    /**
     * Ajoute le joueur dans la liste des joueurs dans le Hub
     * @param player Le joueur à ajouter
     */
    private void addPlayerInHub(Player player) {
        playersInHub.add(player.getUniqueId());
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

    /**
     * Permet d'envoyer le joueur dans le Hub
     * @param player Le joueur à envoyer
     */
    public void sendPlayerToHub(Player player) {
        sendPlayerToSpawn(player);
        player.getInventory().setItem(4, new ItemBuilder(Material.COMPASS).setDisplayName("&rSélectionneur de mini-jeux").toItemStack());
    }

    public void sendPlayerToSpawn(Player player) {
        addPlayerInHub(player);

        player.setGlowing(false);
        player.teleport(spawnLocation);
        player.setBedSpawnLocation(spawnLocation, true);
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.setExhaustion(20);
        player.setHealthScale(20);
        player.setHealth(20);
        player.setLevel(0);
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }


}