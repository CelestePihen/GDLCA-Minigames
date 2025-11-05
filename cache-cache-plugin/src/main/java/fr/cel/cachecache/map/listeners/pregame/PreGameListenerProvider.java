package fr.cel.cachecache.map.listeners.pregame;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.listeners.StateListenerProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && map.isPlayerInMap(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (map.isPlayerInMap(player)) {
            Material type = event.getItemDrop().getItemStack().getType();
            if ((type == Material.AMETHYST_SHARD || type == Material.BARRIER) && !player.isOp()) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractPreGame(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!map.isPlayerInMap(player)) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack item = event.getItem();
            if (item == null) return;

            if (item.getType() == Material.AMETHYST_SHARD) map.startGame(player);
            else if (item.getType() == Material.BARRIER) {
                map.removePlayer(player);
                map.getGameManager().getPlayerManager().sendPlayerToHub(player);
            }
        }
    }
    
}