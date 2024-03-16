package fr.cel.valocraft.manager.arena.state.provider.game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.manager.arena.ValoArena;
import fr.cel.valocraft.manager.arena.state.game.TimeOverArenaState;

public class SpikeListenerProvider extends StateListenerProvider {

    public SpikeListenerProvider(ValoArena arena) {
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
    public void onBlockPlace(BlockPlaceEvent event) {
        if (arena.isPlayerInArena(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!arena.isPlayerInArena(player)) return;

        if (arena.getDefenders().getTeam().isOnTeam(player.getUniqueId()) && event.getBlock().getType() == Material.BREWING_STAND) {
            arena.sendTitle("Spike désamorcé", "");
            arena.addRoundDefender();
            arena.setArenaState(new TimeOverArenaState(arena));
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
            
        if (event.getCause() == DamageCause.PROJECTILE && player.getGameMode() == GameMode.SURVIVAL) {
            arena.eliminate(player);
        }
            
    }
    
}