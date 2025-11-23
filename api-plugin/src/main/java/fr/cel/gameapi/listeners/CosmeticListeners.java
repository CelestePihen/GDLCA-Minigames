package fr.cel.gameapi.listeners;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.cosmetic.CosmeticsManager;
import fr.cel.gameapi.manager.cosmetic.DressingManager;
import fr.cel.gameapi.manager.cosmetic.PlayerCosmetics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

public final class CosmeticListeners implements Listener {

    private final GameAPI main;
    private final CosmeticsManager cosmeticsManager;
    private final DressingManager dressingManager;

    public CosmeticListeners(GameAPI main) {
        this.main = main;
        this.cosmeticsManager = main.getCosmeticsManager();
        this.dressingManager = cosmeticsManager.getDressingManager();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        cosmeticsManager.loadPlayerCosmetics(player).thenAccept(data ->
                Bukkit.getScheduler().runTask(main, () -> cosmeticsManager.reapplyCosmetics(player)));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        dressingManager.undressPlayer(player);
        cosmeticsManager.unloadPlayerCosmetics(player);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(main, () -> cosmeticsManager.reapplyCosmetics(event.getPlayer()), 5L);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(main, () -> {
            PlayerCosmetics data = cosmeticsManager.getPlayerCosmetics(player);
            if (data != null && player.isOnline()) {
                cosmeticsManager.reapplyCosmetics(player);
            }
        }, 2L);
    }

    // Dressing Room Listeners
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (dressingManager.isPlayerDressed(event.getPlayer())) {
            dressingManager.undressPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!dressingManager.isPlayerDressed(player)) return;

        dressingManager.openCosmeticMenu(player);
        event.setCancelled(true);
    }

    @EventHandler
    public void onMannequinDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Mannequin)) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (dressingManager.isPlayerDressed(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (dressingManager.isPlayerDressed(event.getPlayer())) event.setCancelled(true);
    }

}