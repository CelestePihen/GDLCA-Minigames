package fr.cel.valocraft.arena.state.provider;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.arena.ValoArena;

public class StateListenerProvider implements Listener {

    protected final ValoArena arena;

    public StateListenerProvider(ValoArena arena) {
        this.arena = arena;
    }

    public void onEnable(ValoCraft main) {
        main = arena.getGameManager().getMain();
        main.getServer().getPluginManager().registerEvents(this, main);
    }
   
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;
        arena.removePlayer(player);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;
        if (!event.getMessage().contains("/hub")) return;
        arena.removePlayer(player);
    }

    @EventHandler
    public void foodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;

        final Block block = event.getClickedBlock();
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
        }

    }

}
