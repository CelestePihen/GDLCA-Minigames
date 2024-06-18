package fr.cel.cachecache.arena;

import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.arena.state.pregame.StartingArenaState;
import fr.cel.cachecache.utils.TempHubConfig;
import fr.cel.gameapi.utils.ChatUtility;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
    private final List<CCArena> maps;
    private final String chosenHunterMode;
    private final TempHubConfig config;
    private String lastMap;

    private final List<UUID> players;
    @Getter private CCArena chosenMap;

    public TemporaryHub(boolean isActivated, Location location, List<CCArena> maps, CCArena.HunterMode chosenHunterMode, String lastMap, TempHubConfig config, GameManager gameManager) {
        this.isActivated = isActivated;
        this.location = location;
        this.maps = maps;
        this.chosenHunterMode = chosenHunterMode.getName();
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
            player.sendMessage(ChatUtility.format("&6Cache-Cache &r- Il n'y a pas de mode de jeu temporaire actuellement."));
            return;
        }

        if (isPlayerInTempHub(player)) return;

        players.add(player.getUniqueId());
        gameManager.getPlayerManager().removePlayerInHub(player);
        player.getInventory().clear();
        player.teleport(location);
        player.setGameMode(GameMode.ADVENTURE);
        player.sendTitle(ChatUtility.format("&6Cache-Cache &r- " + chosenHunterMode), "Map Aléatoire", 10, 70, 20);
        sendMessage(player.getDisplayName() + " a rejoint le Cache-Cache Temporaire !");
    }

    public void removePlayer(Player player) {
        if (!isPlayerInTempHub(player)) return;
        players.remove(player.getUniqueId());
    }

    public void chooseMapAndSendPlayers(Player player) {
        if (players.size() < 2) {
            player.sendMessage(gameManager.getPrefix() + "Il n'y a pas assez de joueurs (minimum 2 joueurs) !");
            return;
        }

        Collections.shuffle(maps);
        CCArena arena = maps.get(0);

        if (arena.getNameArena().equals(lastMap)) {
            arena = maps.get(1);
        }

        chosenMap = arena;
        setLastMap(chosenMap.getNameArena());

        sendMessage(gameManager.getPrefix() + "La carte a été choisie ! Démarrage de la partie.");
        players.forEach(uuid -> chosenMap.addPlayer(Bukkit.getPlayer(uuid), true));
        players.clear();
        chosenMap.setArenaState(new StartingArenaState(chosenMap));
    }

    public void setActivated(boolean activated) {
        this.isActivated = activated;
        config.setValue("isActivated", activated);
    }

    /**
     * Envoie un message à tous les joueurs dans l'arène
     * @param message Le message à envoyer
     */
    public void sendMessage(String message) {
        message = ChatUtility.format(gameManager.getPrefix() + message);
        for (UUID pls : players) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.sendMessage(message);
        }
    }

    /**
     * Permet de changer le dernier chercheur de l'arène
     * @param lastMap La dernière map à être jouée
     */
    private void setLastMap(String lastMap) {
        this.lastMap = lastMap;
        config.setValue("lastMap", lastMap);
    }

    // Listeners
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player leaver = event.getPlayer();
        if (!isPlayerInTempHub(leaver)) return;
        removePlayer(leaver);
    }

    @EventHandler
    public void foodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!isPlayerInTempHub(player)) return;
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        if (!event.getMessage().contains("/hub")) return;
        if (!isPlayerInTempHub(player)) return;
        removePlayer(player);
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (!isPlayerInTempHub(player)) return;
        if (block == null) return;

        Material type = block.getType();
        
        if (event.getAction() == Action.PHYSICAL && type == Material.FARMLAND) {
            event.setCancelled(true);
            return;
        }

        if (type == Material.FLOWER_POT || block.getType().name().startsWith("POTTED_") || (type == Material.CAVE_VINES || type == Material.CAVE_VINES_PLANT) ||
                type == Material.SWEET_BERRY_BUSH || type == Material.CHEST ||  type == Material.HOPPER || type == Material.FURNACE || type == Material.BLAST_FURNACE ||
                type == Material.SMOKER || type == Material.BARREL || type == Material.DISPENSER || type == Material.DROPPER ||
                type == Material.CHEST_MINECART || type == Material.HOPPER_MINECART || type == Material.FURNACE_MINECART || type == Material.CRAFTING_TABLE) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void playerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (!isPlayerInTempHub(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player player)) return;
        if (!isPlayerInTempHub(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleEntityCollisionEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!isPlayerInTempHub(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player player)) return;
        if (!isPlayerInTempHub(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!isPlayerInTempHub(player)) return;
        if (player.isOp()) return;

        if (event.getInventory().getHolder() instanceof Entity entity) {
            if (entity.getType() == EntityType.CHEST_MINECART || entity.getType() == EntityType.FURNACE_MINECART || entity.getType() == EntityType.HOPPER_MINECART) {
                event.setCancelled(true);
            }
        }
    }

}
