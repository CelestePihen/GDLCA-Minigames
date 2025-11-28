package fr.cel.cachecache.map;

import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.map.state.pregame.StartingMapState;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TemporaryHub implements Listener {

    private final GameManager gameManager;

    @Getter private boolean isActivated;
    private final Location location;
    private final List<CCMap> maps;
    private final String chosenHunterMode;
    private final TempHubConfig config;
    private String lastMap;

    private final List<UUID> players;
    @Getter private CCMap chosenMap;

    public TemporaryHub(boolean isActivated, Location location, List<CCMap> maps, CCMap.CCMode chosenCCMode, String lastMap, TempHubConfig config, GameManager gameManager) {
        this.isActivated = isActivated;
        this.location = location;
        this.maps = maps;
        this.chosenHunterMode = chosenCCMode.getName();
        this.lastMap = lastMap;
        this.config = config;

        this.players = new ArrayList<>();
        this.gameManager = gameManager;
        gameManager.getMain().getServer().getPluginManager().registerEvents(this, gameManager.getMain());
    }

    /**
     * Permet de détecter si le joueur est dans le hub pour le mode temporaire
     * @param player Le joueur à détecter
     */
    public boolean isPlayerInTempHub(Player player) {
        return players.contains(player.getUniqueId());
    }

    public void addPlayer(Player player) {
        if (!isActivated) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Il n'y a pas de mode de jeu temporaire actuellement.")));
            return;
        }

        if (isPlayerInTempHub(player)) return;

        players.add(player.getUniqueId());
        gameManager.getPlayerManager().removePlayerInHub(player);
        player.getInventory().clear();
        player.teleportAsync(location);
        player.setGameMode(GameMode.ADVENTURE);
        player.showTitle(Title.title(gameManager.getPrefix().append(Component.text(chosenHunterMode)), Component.text("Map Aléatoire"), 10, 70, 20));
        sendMessage(player.displayName().append(Component.text(" a rejoint le Cache-Cache Temporaire !")));
    }

    public void removePlayer(Player player) {
        if (!isPlayerInTempHub(player)) return;
        players.remove(player.getUniqueId());
    }

    public void chooseMapAndSendPlayers(Player player) {
        if (players.size() < 2) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Il n'y a pas assez de joueurs (minimum 2 joueurs) !")));
            return;
        }

        Collections.shuffle(maps);
        CCMap map = maps.get(0);

        if (map.getMapName().equals(lastMap)) {
            map = maps.get(1);
        }

        chosenMap = map;
        setLastMap(chosenMap.getMapName());

        sendMessage(gameManager.getPrefix().append(Component.text("La carte a été choisie ! Démarrage de la partie.")));
        players.forEach(uuid -> chosenMap.addPlayer(Bukkit.getPlayer(uuid), true));
        players.clear();
        chosenMap.setMapState(new StartingMapState(chosenMap));
    }

    public void setActivated(boolean activated) {
        this.isActivated = activated;
        config.setValue("isActivated", activated);
    }

    /**
     * Envoie un message à tous les joueurs dans l'arène
     * @param message Le message à envoyer
     */
    public void sendMessage(Component message) {
        message = gameManager.getPrefix().append(message);
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null && player.isOnline()) player.sendMessage(message);
        }
    }

    /**
     * Retourne s'il n'y a pas de map d'installer ou pas
     * @return Vrai s'il n'y a aucune map de mise, et faux s'il y en a au moins une
     */
    public boolean hasNoMaps() {
        return this.maps.isEmpty();
    }

    /**
     * Permet de changer le dernier chercheur de l'arène
     * @param lastMap La dernière map à être jouée
     */
    private void setLastMap(String lastMap) {
        this.lastMap = lastMap;
        if (lastMap != null) {
            this.config.setValue("lastMap", lastMap);
        }
    }

    // Listeners
    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player leaver = event.getPlayer();
        if (!isPlayerInTempHub(leaver)) return;
        removePlayer(leaver);
    }

    @EventHandler
    private void foodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!isPlayerInTempHub(player)) return;
        event.setFoodLevel(20);
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!event.getMessage().contains("/hub")) return;
        if (!isPlayerInTempHub(player)) return;
        removePlayer(player);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isPlayerInTempHub(player)) return;

        if ((event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK)
                || event.getAction() == Action.PHYSICAL)
            event.setCancelled(true);
    }

    @EventHandler
    private void playerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!isPlayerInTempHub(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onVehicleDamage(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player player)) return;
        if (!isPlayerInTempHub(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onVehicleCollision(VehicleEntityCollisionEvent event) {
        if (!(event.getEntity() instanceof Player player) || !isPlayerInTempHub(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player player) || !isPlayerInTempHub(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player) || !isPlayerInTempHub(player)
                || !(event.getInventory().getHolder() instanceof Entity entity)) return;

        EntityType type = entity.getType();
        if (type == EntityType.CHEST_MINECART || type == EntityType.FURNACE_MINECART
                || type == EntityType.HOPPER_MINECART) event.setCancelled(true);
    }

}
