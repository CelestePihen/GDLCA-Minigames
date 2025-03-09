package fr.cel.hub.listener;

import fr.cel.gameapi.GameAPI;
import fr.cel.hub.Hub;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener extends HListener {

    public PlayerListener(Hub main) {
        super(main);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || !GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void playerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player) || player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void damageEntity(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void playerInteractEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager) || event.getEntity() instanceof Player) return;
        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(damager) || damager.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(event.getPlayer()) || event.getPlayer().isOp() || event.getClickedBlock() == null) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) event.setCancelled(true);
    }

    @EventHandler
    public void foodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player) || !GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;
        event.setCancelled(true);
    }
    
}