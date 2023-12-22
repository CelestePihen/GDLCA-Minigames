package fr.cel.hub.listener;

import fr.cel.hub.Hub;
import fr.cel.hub.manager.NPCManager;
import fr.cel.hub.utils.ChatUtility;
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
        final Player player = event.getPlayer();
        
	    if (!player.hasPlayedBefore()) {
            event.setJoinMessage(main.getPrefix() + "Bienvenue à " + player.getName() + " sur le serveur !");
        } else {
            event.setJoinMessage(ChatUtility.format("[&a+&r] ") + player.getName());
        }

        player.setPlayerListHeader(ChatUtility.format("Bienvenue sur &9GDLCA Minigames&f !"));

        main.getPlayerManager().sendPlayerToHub(player);

        NPCManager.getNpcs().forEach(npc -> npc.spawn(player));
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        event.setQuitMessage(ChatUtility.format("[&c-&r] ") + player.getName());

        main.getPlayerManager().removePlayerInHub(player);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!main.getPlayerManager().containsPlayerInHub(player)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!main.getPlayerManager().containsPlayerInHub(event.getPlayer())) return;
        if (event.getPlayer().isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void damageEntity(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!main.getPlayerManager().containsPlayerInHub(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void playerInteractEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (damager instanceof Player && !(entity instanceof Player)) {
            if (!main.getPlayerManager().containsPlayerInHub((Player) damager)) return;
            if (damager.isOp()) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (!main.getPlayerManager().containsPlayerInHub(player)) return;
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
            if (!main.getPlayerManager().containsPlayerInHub(player)) return;
            event.setCancelled(true);
        }
    }
    
}