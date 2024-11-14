package fr.cel.halloween.map.providers.game;

import fr.cel.halloween.HalloweenEvent;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.providers.StateListenerProvider;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayingListenerProvider extends StateListenerProvider {

    public PlayingListenerProvider(HalloweenMap map) {
        super(map);
    }

    @Override
    public void onEnable(HalloweenEvent main) {
        super.onEnable(main);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onDeathAndKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!map.isPlayerInMap(player)) return;
        event.setDeathMessage("");
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (damager instanceof Player pl && entity instanceof Player p) {
            if (!map.isPlayerInMap(pl)) return;
            if (!map.isPlayerInMap(p)) return;

            event.setCancelled(true);

            if (map.getSouls().contains(pl.getUniqueId())) return;

            map.eliminate(p);
            return;
        }

        if (event instanceof ArmorStand) {
            event.setCancelled(true);
        }

        if (damager instanceof Player) {
            if (!map.isPlayerInMap((Player) damager)) return;
            event.setCancelled(true);
        }
    }
    
}