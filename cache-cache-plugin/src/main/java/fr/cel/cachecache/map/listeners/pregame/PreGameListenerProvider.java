package fr.cel.cachecache.map.listeners.pregame;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.listeners.StateListenerProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PreGameListenerProvider extends StateListenerProvider {

    public PreGameListenerProvider(CCMap map) {
        super(map);
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
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && map.isPlayerInMap(player)) event.setCancelled(true);
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (map.isPlayerInMap(player)) {
            if (event.getItemDrop().getItemStack().getType() == Material.AMETHYST_SHARD && !player.isOp()) event.setCancelled(true);
        }
    }
    
}