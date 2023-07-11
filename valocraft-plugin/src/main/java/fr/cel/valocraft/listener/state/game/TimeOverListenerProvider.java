package fr.cel.valocraft.listener.state.game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.listener.state.StateListenerProvider;
import fr.cel.valocraft.manager.arena.Arena;

public class TimeOverListenerProvider extends StateListenerProvider {

    public TimeOverListenerProvider(Arena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (!getArena().isPlayerInArena(player)) return;
            
            if (event.getCause() == DamageCause.PROJECTILE) {
                player.setHealth(0);
            }
            
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!getArena().isPlayerInArena(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!getArena().isPlayerInArena(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDeathAndKill(PlayerDeathEvent event) {
        Player victim =  event.getEntity();
        if (!getArena().isPlayerInArena(victim)) return;

        event.setDeathMessage("");

        if (victim.getGameMode() == GameMode.SURVIVAL) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getArena().getGameManager().getMain(), () -> victim.spigot().respawn(), 10);
            victim.setGameMode(GameMode.SPECTATOR);
            victim.sendMessage(getArena().getGameManager().getPrefix() + "Vous êtes mort(e).");
        }

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!getArena().isPlayerInArena(player)) return;

        Item item = event.getItemDrop();
        if (!(getArena().getAttackers().getTeam().isOnTeam(player.getUniqueId()))) return;
        if (item.getItemStack().getType() != Material.BREWING_STAND) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;

        Player player = (Player) entity;
        if (!getArena().isPlayerInArena(player)) return;

        Item item = event.getItem();
        if (!(getArena().getAttackers().getTeam().isOnTeam(player.getUniqueId()))) return;
        if (item.getItemStack().getType() != Material.BREWING_STAND) event.setCancelled(true);
    }
    
}