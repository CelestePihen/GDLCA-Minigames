package fr.cel.cachecache.arena.listeners.game;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.listeners.StateListenerProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

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

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (!arena.isPlayerInArena(event.getPlayer())) return;
        if (event.getItemDrop().getItemStack().getType() == Material.STICK) event.setCancelled(true);
    }

}