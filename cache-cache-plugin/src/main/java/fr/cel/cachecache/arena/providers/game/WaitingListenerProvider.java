package fr.cel.cachecache.arena.providers.game;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.providers.StateListenerProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class WaitingListenerProvider extends StateListenerProvider {

    public WaitingListenerProvider(CCArena arena) {
        super(arena);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(!arena.isFallDamage());
        }

        event.setCancelled(true);
    }

}