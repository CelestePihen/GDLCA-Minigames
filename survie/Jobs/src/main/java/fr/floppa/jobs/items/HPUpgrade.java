package fr.floppa.jobs.items;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class HPUpgrade implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        double vieJoueur = player.getAttribute(Attribute.MAX_HEALTH).getValue();

        if (player.getInventory().getItemInMainHand().getType() == Material.ENCHANTED_GOLDEN_APPLE && vieJoueur < 30) {
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(vieJoueur + 2);
        }
    }

}