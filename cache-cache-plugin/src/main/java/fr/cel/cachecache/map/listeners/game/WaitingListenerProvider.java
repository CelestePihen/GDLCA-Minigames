package fr.cel.cachecache.map.listeners.game;

import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.listeners.StateListenerProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class WaitingListenerProvider extends StateListenerProvider {

    public WaitingListenerProvider(CCMap map) {
        super(map);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!map.isPlayerInMap(player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(!map.isFallDamage());
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (!map.isPlayerInMap(event.getPlayer())) return;
        if (event.getItemDrop().getItemStack().getType() == Material.STICK) event.setCancelled(true);
    }

}