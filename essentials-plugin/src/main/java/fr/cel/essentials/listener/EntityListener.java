package fr.cel.essentials.listener;

import fr.cel.essentials.commands.other.GodCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!GodCommand.getPlayersInGod().contains(player.getUniqueId())) return;
        event.setCancelled(true);
    }
    
}