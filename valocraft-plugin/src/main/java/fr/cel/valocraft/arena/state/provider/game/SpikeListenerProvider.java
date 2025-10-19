package fr.cel.valocraft.arena.state.provider.game;

import fr.cel.valocraft.Valocraft;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.game.TimeOverArenaState;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SpikeListenerProvider extends StateListenerProvider {

    public SpikeListenerProvider(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(Valocraft main) {
        super.onEnable(main);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (arena.isPlayerInArena(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;

        if (arena.getDefenders().team().containsPlayer(player) && event.getBlock().getType() == Material.BREWING_STAND) {
            arena.sendTitle(Component.text("Spike désamorcé"), Component.empty());
            arena.addRoundDefender();
            arena.setArenaState(new TimeOverArenaState(arena));
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || !arena.isPlayerInArena(player)) return;
            
        if (event.getCause() == DamageCause.PROJECTILE && player.getGameMode() == GameMode.SURVIVAL)
            arena.eliminate(player);
    }
    
}