package fr.cel.hub.listener;

import fr.cel.hub.Hub;
import fr.cel.hub.manager.NPC;
import fr.cel.hub.utils.ChatUtility;
import net.kyori.adventure.text.Component;
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

public class PlayerListener extends HubListener {

    public PlayerListener(Hub main) {
        super(main);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
      final Player player = event.getPlayer();
        
	  if (!player.hasPlayedBefore()) {
          event.joinMessage(Component.text(main.getPrefix() + "Bienvenue à " + player.getName() + " sur le serveur !"));
      } else {
          event.joinMessage(Component.text(ChatUtility.format("[&a+&f] ") + player.getName()));
      }

      player.sendPlayerListHeader(Component.text(ChatUtility.format("Bienvenue sur &9GDLCA Minigames&f !")));

      main.getPlayerManager().sendPlayerToHub(player);

      for (NPC npc : main.getNpcManager().getNpcs()) {
          npc.spawn(player);
      }

    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        event.quitMessage(Component.text(ChatUtility.format("[&c-&f] ") + player.getName()));

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
        Block block = event.getClickedBlock();

        if (!main.getPlayerManager().containsPlayerInHub(player)) return;
        if (player.isOp()) return;
        if (block == null) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) event.setCancelled(true);
        if (block.getType() == Material.FLOWER_POT || block.getType().name().startsWith("POTTED_") || block.getType() == Material.CAVE_VINES || block.getType() == Material.CAVE_VINES_PLANT) event.setCancelled(true);
    }

    @EventHandler
    public void damageEntity(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!main.getPlayerManager().containsPlayerInHub(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void foodChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!main.getPlayerManager().containsPlayerInHub(player)) return;
            event.setCancelled(true);
        }
    }
    
}