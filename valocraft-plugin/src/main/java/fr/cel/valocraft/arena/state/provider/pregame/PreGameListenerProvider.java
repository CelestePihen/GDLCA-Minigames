package fr.cel.valocraft.arena.state.provider.pregame;

import fr.cel.gameapi.GameAPI;
import fr.cel.valocraft.Valocraft;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.inventory.SelectTeam;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PreGameListenerProvider extends StateListenerProvider {

    public PreGameListenerProvider(ValoArena arena) {
        super(arena);
    }

    @Override
    public void onEnable(Valocraft main) {
        super.onEnable(main);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && arena.isPlayerInArena(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (arena.isPlayerInArena(player) && !player.isOp()) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (arena.isPlayerInArena(player) && !player.isOp()) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!arena.isPlayerInArena(player)) return;

        Action action = event.getAction();
        ItemStack itemStack = event.getItem();

        if (itemStack == null || itemStack.getItemMeta() == null || event.getHand() != EquipmentSlot.HAND) return;

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (itemStack.getType().name().contains("WOOL"))
                GameAPI.getInstance().getInventoryManager().openInventory(new SelectTeam(arena.getGameManager()), player);
        }

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!arena.isPlayerInArena(event.getPlayer())) return;

        Material type = event.getItemDrop().getItemStack().getType();
        if (type == Material.WHITE_WOOL || type == Material.RED_WOOL || type == Material.BLUE_WOOL) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player) || !arena.isPlayerInArena(player)) return;
        
        Material type = event.getItem().getItemStack().getType();
        if (type == Material.WHITE_WOOL || type == Material.RED_WOOL || type == Material.BLUE_WOOL) event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerSwapItem(PlayerSwapHandItemsEvent event) {
        if (!arena.isPlayerInArena(event.getPlayer())) return;

        Material type = event.getMainHandItem().getType();
        if (type == Material.WHITE_WOOL || type == Material.RED_WOOL || type == Material.BLUE_WOOL) event.setCancelled(true);
    }
    
}