package fr.cel.cachecache.map.listeners;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.state.game.PlayingMapState;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public abstract class StateListenerProvider implements Listener {

    protected final CCMap map;

    public StateListenerProvider(CCMap map) {
        this.map = map;
    }

    public void onEnable(CacheCache main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }
   
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (map.isPlayerInMap(player)) map.removePlayer(player);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player && map.isPlayerInMap(player)) event.setFoodLevel(20);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (map.isPlayerInMap(player) && event.getMessage().startsWith("/hub")) map.removePlayer(player);
    }

    @EventHandler
    public void onSwappedItem(PlayerSwapHandItemsEvent event) {
        if (map.isPlayerInMap(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!map.isPlayerInMap(player)) return;

        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.FARMLAND)) {
            event.setCancelled(true);
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.AMETHYST_SHARD) {
                map.startGame(player);
            }

            if (event.getClickedBlock() != null && !(map.getMapState() instanceof PlayingMapState) && !player.isOp()) {
                event.setCancelled(true);
            }

            if (event.getClickedBlock() != null && map.getMapState() instanceof PlayingMapState) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || !map.isPlayerInMap(player)) return;

        if (event.getCurrentItem() == null) return;
        Material type = event.getCurrentItem().getType();
        if (type == Material.AIR) return;

        if (type == Material.CARVED_PUMPKIN || (type == Material.AMETHYST_SHARD && !player.isOp())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (map.isPlayerInMap(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && map.isPlayerInMap(damager) &&
                (event.getEntity() instanceof Llama || event.getEntity() instanceof ArmorStand)) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if ((event.getAttacker() instanceof Player player) && map.isPlayerInMap(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleCollision(VehicleEntityCollisionEvent event) {
        if ((event.getEntity() instanceof Player player) && map.isPlayerInMap(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if ((event.getEntered() instanceof Player player) && map.isPlayerInMap(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player) || !map.isPlayerInMap(player) || !(event.getInventory().getHolder() instanceof Entity entity)) return;

        EntityType type = entity.getType();
        if (type == EntityType.CHEST_MINECART || type == EntityType.FURNACE_MINECART || type == EntityType.HOPPER_MINECART) event.setCancelled(true);
    }

}