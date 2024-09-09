package fr.cel.cachecache.arena.state.providers.game;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.state.providers.StateListenerProvider;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        if (!arena.isPlayerInArena(player)) return;

        event.setDeathMessage("");

        if (arena.getTimer() < 30) {
            arena.sendMessage("Le joueur " + player.getName() + " est mort avant les 30 secondes d'attente. Il est donc ressucité.");
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (damager instanceof Player pl && entity instanceof Player p) {
            if (!arena.isPlayerInArena(pl)) return;
            if (!arena.isPlayerInArena(p)) return;

            event.setCancelled(true);

            if (arena.getHiders().contains(pl.getUniqueId())) return;

            arena.eliminate(p);
            return;
        }

        if (event instanceof ArmorStand) {
            event.setCancelled(true);
        }

        if (damager instanceof Player) {
            if (!arena.isPlayerInArena((Player) damager)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;

        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;


        if (arena.getSpawnedGroundItems().contains(item)) {
            arena.getSpawnedGroundItems().remove(item);
            player.sendMessage(arena.getGameManager().getPrefix() + "Vous avez récupéré " + itemMeta.getDisplayName());
        }
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