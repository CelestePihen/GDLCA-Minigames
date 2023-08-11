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
    public void onDamage(EntityDamageEvent event) {
        if (getArena().isPlayerInArena((Player) event.getEntity())) event.setCancelled(true);
    }

}