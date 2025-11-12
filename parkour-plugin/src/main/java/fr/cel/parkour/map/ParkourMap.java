package fr.cel.parkour.map;

import fr.cel.gameapi.GameAPI;
import fr.cel.parkour.manager.GameManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ParkourMap implements Listener {
    
    @Getter private final String mapName;
    @Getter private final String displayName;

    @Getter private final Location spawnLoc;

    @Getter private final Set<UUID> players;

    public ParkourMap(String mapName, String displayName, Location spawnLoc, GameManager gameManager) {
        this.mapName = mapName;
        this.displayName = displayName;
        this.spawnLoc = spawnLoc;
        this.players = new HashSet<>();

        gameManager.getMain().getServer().getPluginManager().registerEvents(this, gameManager.getMain());
    }

    public void addPlayer(Player player) {
        if (players.contains(player.getUniqueId())) return;

        GameAPI.getInstance().getPlayerManager().removePlayerInHub(player);
        players.add(player.getUniqueId());
        sendMessage(player.displayName().append(Component.text(" a rejoint le parkour !")));

        player.setRespawnLocation(spawnLoc, true);
        player.teleportAsync(this.getSpawnLoc());
        player.showTitle(Title.title(Component.text("Parkour", NamedTextColor.GOLD), Component.text(this.displayName)));
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);

        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 255, false, false, false));
    }

    public void removePlayer(Player player) {
        if (!players.contains(player.getUniqueId())) return;
        this.getPlayers().remove(player.getUniqueId());
        GameAPI.getInstance().getPlayerManager().sendPlayerToHub(player);
    }

    public boolean isPlayerInMap(Player player) {
        return players.contains(player.getUniqueId());
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.isPlayerInMap(player)) this.removePlayer(player);
    }

    @EventHandler
    private void quit(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (isPlayerInMap(player) && event.getMessage().contains("/hub")) this.removePlayer(player);
    }

    @EventHandler
    private void fallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (isPlayerInMap(player) && event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
    }

    @EventHandler
    private void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (isPlayerInMap(player)) event.setCancelled(true);
    }

    private void sendMessage(Component message) {
        message = GameManager.getPrefix().append(message);
        for (UUID pls : this.getPlayers()) {
            Player player = Bukkit.getPlayer(pls);
            if (player != null) player.sendMessage(message);
        }
    }

}