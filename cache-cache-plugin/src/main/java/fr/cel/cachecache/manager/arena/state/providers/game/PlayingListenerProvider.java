package fr.cel.cachecache.manager.arena.state.providers.game;

import org.bukkit.event.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.CCArena;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.manager.arena.state.providers.StateListenerProvider;

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

        Player victim = event.getEntity();
        if (!getArena().isPlayerInArena(victim)) return;

        if (event.getEntity().getKiller() instanceof Player && victim.getGameMode() == GameMode.ADVENTURE) {
            Player killer = event.getEntity().getKiller();
            if (!getArena().isPlayerInArena(killer)) return;
            if (getArena().getSeekers().contains(victim.getUniqueId())) return;

            getArena().eliminate(victim);
            
            event.setDeathMessage("");
            getArena().sendMessage(killer.getName() + " a tué " + victim.getName());
            return;
        }
        
        else if (getArena().getTimer() < 30) {
            event.setDeathMessage("");
            getArena().sendMessage("Le joueur " + victim.getName() + " est mort avant les 30 secondes d'attente. Il est donc ressucité.");
            return;
        }

        else {
            event.setDeathMessage("");
            getArena().sendMessage(victim.getName() + " n'est pas mort par le tueur. Il est donc ressucité.");
        }
    }

    @EventHandler
    public void playerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (!getArena().isPlayerInArena(player)) return;

        Item item = event.getItem();

        if (getArena().getSpawnedGroundItems().contains(item)) {
            getArena().getSpawnedGroundItems().remove(item);
            player.sendMessage(GameManager.getPrefix() + "Vous avez récupéré " + item.getItemStack().getItemMeta().getDisplayName());
        }

    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (damager instanceof Player && (!(entity instanceof Player) || entity instanceof ArmorStand)) {
            if (!getArena().isPlayerInArena((Player) damager)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (!getArena().isPlayerInArena(player)) return;

            if (event.getView().getTitle().equalsIgnoreCase("Joueurs")) {
                ItemStack itemStack = event.getCurrentItem();
                String name = itemStack.getItemMeta().getDisplayName();
                if (getArena().getPlayers().contains(Bukkit.getPlayer(name).getUniqueId())) {
                    Player target = Bukkit.getPlayer(name);
                    Location tempLocation = player.getLocation();

                    player.teleport(target.getLocation());
                    target.teleport(tempLocation);

                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if (itemInHand.getAmount() == 1) player.getInventory().setItemInMainHand(null);
                    else itemInHand.setAmount(itemInHand.getAmount() - 1);
                    return;
                } else {
                    player.sendMessage(GameManager.getPrefix() + "Ce joueur n'est pas disponible. Merci de réouvrir le menu.");
                    player.closeInventory();
                    return;
                }
            }
        }
    }
    
}