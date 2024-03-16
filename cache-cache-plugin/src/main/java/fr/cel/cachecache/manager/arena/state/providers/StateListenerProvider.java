package fr.cel.cachecache.manager.arena.state.providers;

import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.arena.state.game.PlayingArenaState;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.GroundItem;
import lombok.Getter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class StateListenerProvider implements Listener {

    @Getter private final CCArena arena;

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
        Player leaver = event.getPlayer();
        if (!arena.isPlayerInArena(leaver)) return;
        getArena().removePlayer(leaver);
    }

    @EventHandler
    public void foodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!arena.isPlayerInArena(player)) return;

        if (message.equalsIgnoreCase("/hub") || message.equalsIgnoreCase("/hub:hub") || message.equalsIgnoreCase("/hub " + player.getName()) || message.equalsIgnoreCase("/hub:hub " + player.getName())) {
            arena.removePlayer(player);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        
        if (!arena.isPlayerInArena(player)) return;

        if (block == null) return;

        Material type = block.getType();

        if (event.getAction() == Action.PHYSICAL && type == Material.FARMLAND) {
            event.setCancelled(true);
            return;
        }

        if (type == Material.FLOWER_POT || block.getType().name().startsWith("POTTED_") || (type == Material.CAVE_VINES || type == Material.CAVE_VINES_PLANT) ||
                type == Material.SWEET_BERRY_BUSH || type == Material.CHEST ||  type == Material.HOPPER || type == Material.FURNACE || type == Material.BLAST_FURNACE ||
                type == Material.SMOKER || type == Material.BARREL || type == Material.DISPENSER || type == Material.DROPPER ||
                type == Material.CHEST_MINECART || type == Material.HOPPER_MINECART || type == Material.FURNACE_MINECART || type == Material.CRAFTING_TABLE) {
            event.setCancelled(true);
            return;
        }

        if (type == Material.LEVER && arena.getArenaState() instanceof PlayingArenaState && lever(block.getLocation())) {
            if (block.getBlockData() instanceof Powerable powerable) {
                if (powerable.isPowered()) {
                    arena.getHiders().forEach(uuid -> Bukkit.getPlayer(uuid).getInventory().setHelmet(new ItemStack(Material.AIR)));
                    arena.getSeekers().forEach(uuid -> Bukkit.getPlayer(uuid).setGlowing(true));
                } else {
                    arena.getHiders().forEach(uuid -> Bukkit.getPlayer(uuid).getInventory().setHelmet(new ItemBuilder(Material.CARVED_PUMPKIN).setDisplayName("Masque").toItemStack()));
                    arena.getSeekers().forEach(uuid -> Bukkit.getPlayer(uuid).setGlowing(false));
                }
            }
        }

        ItemStack itemStack = event.getItem();

        for (GroundItem groundItem : arena.getAvailableGroundItems()) {
            if (groundItem != null && itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(groundItem.getDisplayName())) {
                groundItem.onInteract(player, arena);
            }
        }

    }

    private boolean lever(Location locationBlock) {
        if (locationBlock.getBlockX() == getArena().getLeverLocation().getBlockX()) {
            if (locationBlock.getBlockY() == getArena().getLeverLocation().getBlockY()) {
                if (locationBlock.getBlockZ() == getArena().getLeverLocation().getBlockZ()) {
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        if (itemStack.getType() == Material.CARVED_PUMPKIN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleEntityCollisionEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        if (player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player player) {
            if (!arena.isPlayerInArena(player)) return;
            if (player.isOp()) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof Entity entity) {
            if (entity.getType() == EntityType.MINECART_CHEST || entity.getType() == EntityType.MINECART_FURNACE || entity.getType() == EntityType.MINECART_HOPPER) {
                event.setCancelled(true);
            }
        }
    }

}