package fr.cel.valocraft.arena.state.provider.game;

import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class WaitingListenerProvider extends StateListenerProvider {

    public WaitingListenerProvider(ValoArena arena) {
        super(arena);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (arena.isPlayerInArena(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (arena.isPlayerInArena(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (arena.isPlayerInArena(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;
        if (!arena.getAttackers().team().containsPlayer(player)) return;
        if (event.getItemDrop().getItemStack().getType() != Material.BREWING_STAND) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        if (!arena.getAttackers().team().containsPlayer(player)) return;

        if (event.getItem().getItemStack().getType() != Material.BREWING_STAND) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;

        if (event.getTo().getBlock().getType() == Material.STRUCTURE_VOID)
            player.teleport(arena.getRoleByPlayer(player).spawn());
    }

}