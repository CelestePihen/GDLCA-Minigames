package fr.cel.valocraft.manager.arena.state.provider.pregame;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.manager.arena.ValoArena;

public class StartingListenerProvider extends StateListenerProvider {

    public StartingListenerProvider(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(ValoCraft main) {
        super.onEnable(main);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!getArena().isPlayerInArena(player)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!getArena().isPlayerInArena(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!getArena().isPlayerInArena(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void rightClickItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        Action action = event.getAction();
        
        if (!getArena().isPlayerInArena(player)) return;
        if (!event.hasItem()) return;
        if (!itemStack.hasItemMeta()) return;
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        if (itemStack.getItemMeta().getDisplayName().equalsIgnoreCase("Sélecteur d'équipes")) {
            player.sendMessage(getArena().getGameManager().getPrefix() + "Vous n'avez pas le droit de changer d'équipe quand la partie est lancée.");
        }

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!getArena().isPlayerInArena(player)) return;

        Item item = event.getItemDrop();
        if (item.getItemStack().getType() != Material.WHITE_WOOL
        || item.getItemStack().getType() != Material.RED_WOOL
        || item.getItemStack().getType() != Material.BLUE_WOOL
        || item.getItemStack().getType() != Material.ORANGE_WOOL) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (!getArena().isPlayerInArena(player)) return;

        Item item = event.getItem();
        if (item.getItemStack().getType() != Material.WHITE_WOOL
        || item.getItemStack().getType() != Material.RED_WOOL
        || item.getItemStack().getType() != Material.BLUE_WOOL
        || item.getItemStack().getType() != Material.ORANGE_WOOL) event.setCancelled(true);
    }
    
}