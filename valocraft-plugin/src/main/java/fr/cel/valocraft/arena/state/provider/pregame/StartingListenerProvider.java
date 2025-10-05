package fr.cel.valocraft.arena.state.provider.pregame;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.provider.StateListenerProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

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
        if (!(event.getEntity() instanceof Player player)) return;
        if (arena.isPlayerInArena(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (arena.isPlayerInArena(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (arena.isPlayerInArena(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();
        
        if (!arena.isPlayerInArena(player)) return;
        if (itemStack == null) return;
        if (itemStack.getItemMeta() == null) return;

        String itemName = ((TextComponent) itemStack.getItemMeta().itemName()).content();
        if (itemName.equals("Sélecteur d'équipes"))
            player.sendMessage(arena.getGameManager().getPrefix().append(Component.text("Vous n'avez pas le droit de changer d'équipe quand la partie est lancée.")));
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!arena.isPlayerInArena(event.getPlayer())) return;

        Material type = event.getItemDrop().getItemStack().getType();
        if (type == Material.WHITE_WOOL || type == Material.RED_WOOL || type == Material.BLUE_WOOL) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!arena.isPlayerInArena(player)) return;

        Material type = event.getItem().getItemStack().getType();
        if (type == Material.WHITE_WOOL || type == Material.RED_WOOL || type == Material.BLUE_WOOL) event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerSwapItem(PlayerSwapHandItemsEvent event) {
        Material type = event.getMainHandItem().getType();
        if (type == Material.WHITE_WOOL || type == Material.RED_WOOL || type == Material.BLUE_WOOL) event.setCancelled(true);
    }
    
}