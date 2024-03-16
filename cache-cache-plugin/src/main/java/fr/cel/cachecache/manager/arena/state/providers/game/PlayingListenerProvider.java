package fr.cel.cachecache.manager.arena.state.providers.game;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.arena.state.providers.StateListenerProvider;

public class PlayingListenerProvider extends StateListenerProvider {

    public PlayingListenerProvider(CCArena arena) {
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
    public void onDeathAndKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!getArena().isPlayerInArena(player)) return;

        event.setDeathMessage("");

        if (getArena().getTimer() < 30) {
            getArena().sendMessage("Le joueur " + player.getName() + " est mort avant les 30 secondes d'attente. Il est donc ressucité.");
        }
    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (damager instanceof Player pl && entity instanceof Player p) {
            if (!getArena().isPlayerInArena(pl)) return;
            if (!getArena().isPlayerInArena(p)) return;

            event.setCancelled(true);

            if (getArena().getHiders().contains(pl.getUniqueId())) return;

            getArena().eliminate(p);
        }

        if (damager instanceof Player && !(entity instanceof Player) || entity instanceof ArmorStand) {
            if (!getArena().isPlayerInArena((Player) damager)) return;
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void playerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!getArena().isPlayerInArena(player)) return;

        Item item = event.getItem();

        if (getArena().getSpawnedGroundItems().contains(item)) {
            getArena().getSpawnedGroundItems().remove(item);
            player.sendMessage(getArena().getGameManager().getPrefix() + "Vous avez récupéré " + item.getItemStack().getItemMeta().getDisplayName());
        }
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