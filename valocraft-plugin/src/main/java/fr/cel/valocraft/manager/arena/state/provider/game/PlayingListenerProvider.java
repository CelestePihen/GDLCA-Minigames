package fr.cel.valocraft.manager.arena.state.provider.game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
import org.bukkit.inventory.ItemStack;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.manager.arena.ValoArena;
import fr.cel.valocraft.manager.arena.state.game.SpikeArenaState;

public class PlayingListenerProvider extends StateListenerProvider {

    public PlayingListenerProvider(ValoArena arena) {
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

        if (event.getBlockAgainst().getType() != Material.GREEN_WOOL) {
            event.setCancelled(true);
            return;
        } else {
            getArena().setSpike(event.getBlock());
            getArena().setArenaState(new SpikeArenaState(getArena()));
            return;
        }
        
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!getArena().isPlayerInArena(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim =  event.getEntity();
        if (!getArena().isPlayerInArena(victim)) return;

        event.setDeathMessage("");

        if (victim.getGameMode() == GameMode.SURVIVAL) {
            if (getArena().getAttackers().getTeam().isOnTeam(victim.getUniqueId()) && victim.getInventory().contains(new ItemStack(Material.BREWING_STAND))) {
                victim.getWorld().dropItem(victim.getLocation(), new ItemStack(Material.BREWING_STAND));
            }
            getArena().eliminate(victim);
            return;
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!getArena().isPlayerInArena(player)) return;

        Item item = event.getItemDrop();
        if (!(getArena().getAttackers().getTeam().isOnTeam(player.getUniqueId()))) return;
        if (item.getItemStack().getType() != Material.BREWING_STAND) event.setCancelled(true); return;
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