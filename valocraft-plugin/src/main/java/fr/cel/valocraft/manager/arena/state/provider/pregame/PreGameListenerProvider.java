package fr.cel.valocraft.manager.arena.state.provider.pregame;

import fr.cel.gameapi.GameAPI;
import fr.cel.valocraft.manager.inventory.SelectTeam;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.manager.arena.ValoArena;
import fr.cel.gameapi.utils.ChatUtility;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PreGameListenerProvider extends StateListenerProvider {

    public PreGameListenerProvider(ValoArena arena) {
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
            if (!arena.isPlayerInArena(player)) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!arena.isPlayerInArena(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!arena.isPlayerInArena(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;
        Action action = event.getAction();
        ItemStack itemStack = event.getItem();

        if (itemStack == null) return;
        if (itemStack.getItemMeta() == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (itemStack.getItemMeta().getDisplayName().equals("Sélecteur d'équipes")) {
                GameAPI.getInstance().getInventoryManager().openInventory(new SelectTeam(), player);
            }
        }

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!arena.isPlayerInArena(event.getPlayer())) return;

        Item item = event.getItemDrop();
        if (item.getItemStack().getType() != Material.WHITE_WOOL
        || item.getItemStack().getType() != Material.RED_WOOL
        || item.getItemStack().getType() != Material.BLUE_WOOL) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;
        
        Item item = event.getItem();
        if (item.getItemStack().getType() != Material.WHITE_WOOL || item.getItemStack().getType() != Material.RED_WOOL ||
                item.getItemStack().getType() != Material.BLUE_WOOL || item.getItemStack().getType() != Material.ORANGE_WOOL) event.setCancelled(true);
    }
    
}