package fr.cel.cachecache.manager.arena.state.providers.game;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.arena.state.providers.StateListenerProvider;

public class WaitingListenerProvider extends StateListenerProvider {

    public WaitingListenerProvider(CCArena arena) {
        super(arena);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!getArena().isPlayerInArena(player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(!getArena().isFallDamage());
        }

        event.setCancelled(true);
    }

}