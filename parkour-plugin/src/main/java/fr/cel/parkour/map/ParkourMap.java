package fr.cel.parkour.map;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.parkour.manager.GameManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ParkourMap implements Listener {
    
    private final GameManager gameManager;

    @Getter private final String nameArea;
    @Getter private final String displayName;

    @Getter private final Location spawnLoc;

    @Getter private final Set<UUID> players;

    public ParkourMap(String nameArea, String displayName, Location spawnLoc, GameManager gameManager) {
        this.nameArea = nameArea;
        this.displayName = displayName;
        this.spawnLoc = spawnLoc;
        this.players = new HashSet<>();

        this.gameManager = gameManager;
        gameManager.getMain().getServer().getPluginManager().registerEvents(this, gameManager.getMain());
    }

    public void addPlayer(Player player) {
        if (players.contains(player.getUniqueId())) return;

        GameAPI.getInstance().getPlayerManager().removePlayerInHub(player);
        players.add(player.getUniqueId());
        sendMessage(player.getDisplayName() + " a rejoint le parkour !");

        player.setRespawnLocation(spawnLoc, true);
        player.teleport(this.getSpawnLoc());
        player.sendTitle(ChatUtility.format("&6Parkour"), this.getDisplayName(), 10, 70, 20);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
    }

    public void removePlayer(Player player) {
        if (!players.contains(player.getUniqueId())) return;
        this.getPlayers().remove(player.getUniqueId());
        GameAPI.getInstance().getPlayerManager().sendPlayerToHub(player);
    }

    public boolean isPlayerInArena(Player player) {
        return players.contains(player.getUniqueId());
    }

    @EventHandler
    private void quit(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().contains("/hub")) {
            if (!this.isPlayerInArena(player)) return;
            this.removePlayer(player);
        }
    }

    @EventHandler
    private void fallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!this.isPlayerInArena(player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
    }

    @EventHandler
    private void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!this.isPlayerInArena(player)) return;
        event.setCancelled(true);
    }

    private void sendMessage(String message) {
        message = gameManager.getPrefix() + ChatUtility.format(message);
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player == null) continue;
            player.sendMessage(message);
        }
    }

}