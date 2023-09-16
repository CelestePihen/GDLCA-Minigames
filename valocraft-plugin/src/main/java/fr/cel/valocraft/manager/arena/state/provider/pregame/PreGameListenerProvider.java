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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.manager.arena.state.provider.StateListenerProvider;
import fr.cel.valocraft.manager.arena.ValoArena;
import fr.cel.hub.utils.ChatUtility;
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
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!getArena().isPlayerInArena(player)) return;
        Action action = event.getAction();
        ItemStack itemStack = event.getItem();

        if (itemStack == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (itemStack.getItemMeta().getDisplayName().equals("Sélecteur d'équipes")) {
                player.openInventory(getArena().getGameManager().getInventories().get("selectteam").getInv());
            }
        }

    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!getArena().isPlayerInArena(player)) return;

        if (event.getView().getTitle().contains("Sélecteur d'équipes")) {

            if (event.getCurrentItem() == null) return;

            switch (event.getCurrentItem().getType()) {
                case WHITE_WOOL -> {
                    getArena().getBlueTeam().removePlayer(player);
                    getArena().getRedTeam().removePlayer(player);
                    getArena().sendMessage(player.getDisplayName() + " est maintenant dans aucune équipe.");
                    player.sendTitle(ChatUtility.format("Vous êtes maintenant dans aucune équipe."), "", 10, 70, 20);
                    player.getInventory().getItemInMainHand().setType(Material.WHITE_WOOL);
                    player.closeInventory();
                }

                case RED_WOOL -> {
                    getArena().getBlueTeam().removePlayer(player);
                    getArena().getRedTeam().addPlayer(player);
                    getArena().sendMessage(player.getDisplayName() + " a rejoint l'&céquipe rouge&f.");
                    player.sendTitle(ChatUtility.format("Vous avez rejoint l'&céquipe rouge&f."), "", 10, 70, 20);
                    player.getInventory().getItemInMainHand().setType(Material.RED_WOOL);
                    player.closeInventory();
                }

                case BLUE_WOOL -> {
                    getArena().getRedTeam().removePlayer(player);
                    getArena().getBlueTeam().addPlayer(player);
                    getArena().sendMessage(player.getDisplayName() + " a rejoint l'&1équipe bleue&f.");
                    player.sendTitle(ChatUtility.format("Vous avez rejoint l'&1équipe bleue&f."), "", 10, 70, 20);
                    player.getInventory().getItemInMainHand().setType(Material.BLUE_WOOL);
                    player.closeInventory();
                }
                default -> {}
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!getArena().isPlayerInArena(player)) return;

        Item item = event.getItemDrop();
        if (item.getItemStack().getType() != Material.WHITE_WOOL
        || item.getItemStack().getType() != Material.RED_WOOL
        || item.getItemStack().getType() != Material.BLUE_WOOL) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (!getArena().isPlayerInArena(player)) return;

        Item item = event.getItem();
        if (item.getItemStack().getType() != Material.WHITE_WOOL
        || item.getItemStack().getType() != Material.RED_WOOL
        || item.getItemStack().getType() != Material.BLUE_WOOL) event.setCancelled(true);
    }
    
}