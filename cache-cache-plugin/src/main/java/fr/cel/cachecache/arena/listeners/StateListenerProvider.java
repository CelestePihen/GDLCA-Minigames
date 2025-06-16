package fr.cel.cachecache.arena.listeners;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.state.game.PlayingArenaState;
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

    protected final CCArena arena;

    public StateListenerProvider(CCArena arena) {
        this.arena = arena;
    }

    public void onEnable(CacheCache main) {
        main = arena.getGameManager().getMain();
        main.getServer().getPluginManager().registerEvents(this, main);
    }
   
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (arena.isPlayerInArena(player)) arena.removePlayer(player);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player && arena.isPlayerInArena(player)) event.setFoodLevel(20);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (arena.isPlayerInArena(player) && event.getMessage().contains("/hub")) arena.removePlayer(player);
    }

    @EventHandler
    public void onSwappedItem(PlayerSwapHandItemsEvent event) {
        if (arena.isPlayerInArena(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (arena.isPlayerInArena(player) && !player.isOp()
                && event.getItemDrop().getItemStack().getType() == Material.AMETHYST_SHARD) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;

        if (event.getAction() == Action.PHYSICAL) {
            event.setCancelled(true);
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (arena.getArenaState() instanceof PlayingArenaState) {
                event.setCancelled(true);
            }
            else if (!player.isOp()) {
                event.setCancelled(true);
            }
        }

        if (event.getItem() != null && event.getItem().getType() == Material.AMETHYST_SHARD) {
            arena.startGame(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || !arena.isPlayerInArena(player)) return;

        if (event.getCurrentItem() == null) return;
        Material type = event.getCurrentItem().getType();
        if (type == Material.AIR) return;

        if (type == Material.CARVED_PUMPKIN || (type == Material.AMETHYST_SHARD && !player.isOp())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (arena.isPlayerInArena(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && arena.isPlayerInArena(damager) &&
                (event.getEntity() instanceof Llama || event.getEntity() instanceof ArmorStand)) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if ((event.getAttacker() instanceof Player player) && arena.isPlayerInArena(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleCollision(VehicleEntityCollisionEvent event) {
        if ((event.getEntity() instanceof Player player) && arena.isPlayerInArena(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if ((event.getEntered() instanceof Player player) && arena.isPlayerInArena(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player) || !arena.isPlayerInArena(player) || !(event.getInventory().getHolder() instanceof Entity entity)) return;

        EntityType type = entity.getType();
        if (type == EntityType.CHEST_MINECART || type == EntityType.FURNACE_MINECART || type == EntityType.HOPPER_MINECART) event.setCancelled(true);
    }

}