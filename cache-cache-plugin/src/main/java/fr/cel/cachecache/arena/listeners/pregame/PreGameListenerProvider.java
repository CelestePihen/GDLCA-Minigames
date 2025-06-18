package fr.cel.cachecache.arena.listeners.pregame;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.listeners.StateListenerProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PreGameListenerProvider extends StateListenerProvider {

    public PreGameListenerProvider(CCArena arena) {
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

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (arena.isPlayerInArena(player)) {
            if (event.getItemDrop().getItemStack().getType() == Material.AMETHYST_SHARD && !player.isOp()) event.setCancelled(true);
        }
    }
    
}