package fr.cel.cachecache.manager.arena.state.providers;

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
import fr.cel.cachecache.manager.CCArena;
import fr.cel.cachecache.manager.GroundItem;
import lombok.Getter;

public abstract class StateListenerProvider implements Listener {

    @Getter private CCArena arena;

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
        if (!getArena().isPlayerInArena(leaver)) return;
        getArena().removePlayer(leaver);
    }

    @EventHandler
    public void foodChange(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            if (!getArena().isPlayerInArena((Player) entity)) return;
            event.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equals("/hub") || event.getMessage().equals("/hub:hub")) {
            if (!getArena().isPlayerInArena(player)) return;
            arena.removePlayer(player);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        ItemStack itemStack = event.getItem();
        Player player = event.getPlayer();
        
        if (!getArena().isPlayerInArena(event.getPlayer())) return;
        if (block == null) return;

        if (event.getAction() == Action.PHYSICAL && (event.getClickedBlock().getType() == Material.FARMLAND)) event.setCancelled(true);

        if (block.getType() == Material.FLOWER_POT || block.getType().name().startsWith("POTTED_") ||
        (block.getType() == Material.CAVE_VINES || block.getType() == Material.CAVE_VINES_PLANT) ||
        block.getType() == Material.SWEET_BERRY_BUSH || block.getType() == Material.CHEST || 
        block.getType() == Material.HOPPER || block.getType() == Material.FURNACE || 
        block.getType() == Material.BLAST_FURNACE || block.getType() == Material.SMOKER) event.setCancelled(true);

        for (GroundItem groundItem : arena.getAvailableGroundItems()) {
            if (itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName() != null
                    && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(groundItem.getDisplayName())) {
                groundItem.onInteract(player, arena);
            }
        }

    }

    @EventHandler
    public void playerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!getArena().isPlayerInArena(event.getPlayer())) return;
        if (event.getPlayer().isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void mountEntity(EntityMountEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            if (!getArena().isPlayerInArena((Player) entity)) return;
            if (event.getMount() instanceof AbstractHorse) event.setCancelled(true);
        }
    }

}