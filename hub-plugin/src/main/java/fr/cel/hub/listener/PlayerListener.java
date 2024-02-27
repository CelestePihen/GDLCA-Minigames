package fr.cel.hub.listener;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.hub.Hub;
import fr.cel.hub.manager.NPCManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener extends HListener {

    public PlayerListener(Hub main) {
        super(main);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        NPCManager.getNpcs().forEach(npc -> npc.spawn(event.getPlayer()));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(event.getPlayer())) return;
        if (event.getPlayer().isOp()) return;
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
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (damager instanceof Player && !(entity instanceof Player)) {
            if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub((Player) damager)) return;
            if (damager.isOp()) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;
        if (player.isOp()) return;
        if (block == null) return;
        final Material type = block.getType();
        if (type == Material.LECTERN) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) event.setCancelled(true);
        if (type == Material.FLOWER_POT || type.name().startsWith("POTTED_") || type == Material.CAVE_VINES || type == Material.CAVE_VINES_PLANT) event.setCancelled(true);
    }

    @EventHandler
    public void foodChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;
            event.setCancelled(true);
        }
    }
    
}