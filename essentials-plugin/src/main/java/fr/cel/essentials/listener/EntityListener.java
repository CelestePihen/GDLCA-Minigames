package fr.cel.essentials.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import fr.cel.essentials.Essentials;

public class EntityListener implements Listener {

    private final Essentials main;

    public EntityListener(Essentials main) {
        this.main = main;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            if (main.containsPlayersInGod((Player) entity)) {
                event.setCancelled(true);
            }
        }

    }
    
}
