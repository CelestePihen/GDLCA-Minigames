package fr.cel.valocraft.listener.state.game;

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
import fr.cel.valocraft.listener.state.StateListenerProvider;
import fr.cel.valocraft.manager.arena.Arena;
import fr.cel.valocraft.manager.arena.state.game.TimeOverArenaState;

public class SpikeListenerProvider extends StateListenerProvider {

    public SpikeListenerProvider(Arena arena) {
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
        Player player = event.getPlayer();
        if (getArena().isPlayerInArena(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!getArena().isPlayerInArena(player)) return;
        if (block == null) return;

        if (getArena().getDefenders().getTeam().isOnTeam(player.getUniqueId())) {

            if (block.getType() == Material.BREWING_STAND) {
                getArena().sendTitle("Spike désamorcé", "");
                getArena().setSpike(null);
                getArena().addRoundDefender();
                getArena().setArenaState(new TimeOverArenaState(getArena()));
                return;
            } else {
                event.setCancelled(true);
                return;
            }

        } else {
            event.setCancelled(true);
            return;
        }

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
    public void onDeath(PlayerDeathEvent event) {
        Player victim =  event.getEntity();
        if (!getArena().isPlayerInArena(victim)) return;

        event.setDeathMessage("");

        if (victim.getGameMode() == GameMode.SURVIVAL) {
            getArena().eliminate(victim);
        }
    }
    
}