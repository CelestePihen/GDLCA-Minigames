package fr.cel.cachecache.arena.listeners.pregame;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.listeners.StateListenerProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class StartingListenerProvider extends StateListenerProvider {

    public StartingListenerProvider(CCArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(CacheCache main) {
        super.onEnable(main);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && arena.isPlayerInArena(player)) event.setCancelled(true);
    }
    
}