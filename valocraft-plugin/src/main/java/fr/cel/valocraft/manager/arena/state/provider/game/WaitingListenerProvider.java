package fr.cel.valocraft.manager.arena.state.provider.game;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.cel.valocraft.manager.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.manager.arena.ValoArena;

public class WaitingListenerProvider extends StateListenerProvider {

    public WaitingListenerProvider(ValoArena arena) {
        super(arena);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (getArena().isPlayerInArena(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (getArena().isPlayerInArena(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (getArena().isPlayerInArena(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!getArena().isPlayerInArena(player)) return;
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getMaterial() == Material.SPLASH_POTION) {
                event.setCancelled(true);
            }
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
        if (!(entity instanceof Player player)) return;
        if (!getArena().isPlayerInArena(player)) return;

        Item item = event.getItem();
        if (!(getArena().getAttackers().getTeam().isOnTeam(player.getUniqueId()))) return;
        if (item.getItemStack().getType() != Material.BREWING_STAND) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!getArena().isPlayerInArena(player)) return;

        if (event.getTo().getBlock().getType() == Material.STRUCTURE_VOID) {
            player.teleport(getArena().getRoleByPlayer(player).getSpawn());
        }
    }

}