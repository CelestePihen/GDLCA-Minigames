package fr.cel.cachecache.manager.arena.state.providers;

import fr.cel.cachecache.manager.arena.CCArena;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;

import fr.cel.cachecache.CacheCache;
import fr.cel.cachecache.manager.GroundItem;
import lombok.Getter;

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
        if (event.getEntity() instanceof Player player) {
            if (!arena.isPlayerInArena(player)) return;
            event.setFoodLevel(20);
        }
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
        ItemStack itemStack = event.getItem();
        Player player = event.getPlayer();
        Action action = event.getAction();
        
        if (!arena.isPlayerInArena(player)) return;

        if (block == null) return;

        Material type = block.getType();

        if (event.getAction() == Action.PHYSICAL && type == Material.FARMLAND) event.setCancelled(true);

        if (type == Material.FLOWER_POT || block.getType().name().startsWith("POTTED_") || (type == Material.CAVE_VINES || type == Material.CAVE_VINES_PLANT) ||
                type == Material.SWEET_BERRY_BUSH || type == Material.CHEST ||  type == Material.HOPPER || type == Material.FURNACE ||
                type == Material.BLAST_FURNACE || type == Material.SMOKER) event.setCancelled(true);

        for (GroundItem groundItem : arena.getAvailableGroundItems()) {
            if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                    && itemStack != null
                    && itemStack.getItemMeta() != null
                    && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(groundItem.getDisplayName())) {
                groundItem.onInteract(player, arena);
            }
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
    public void mountEntity(EntityMountEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!getArena().isPlayerInArena(player)) return;
            if (event.getMount() instanceof AbstractHorse) event.setCancelled(true);
        }
    }

}