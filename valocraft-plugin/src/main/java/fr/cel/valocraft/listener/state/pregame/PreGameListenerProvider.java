package fr.cel.valocraft.listener.state.pregame;

import org.bukkit.Bukkit;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.cel.valocraft.ValoCraft;
import fr.cel.valocraft.listener.state.StateListenerProvider;
import fr.cel.valocraft.manager.arena.Arena;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.hub.utils.ItemBuilder;

public class PreGameListenerProvider extends StateListenerProvider {

    public PreGameListenerProvider(Arena arena) {
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
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (getArena().isPlayerInArena(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (getArena().isPlayerInArena(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (getArena().isPlayerInArena(player)) {
            event.setCancelled(true);
        }
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

            Inventory menu = null;

            switch (itemStack.getType()) {
                case WHITE_WOOL:
                case RED_WOOL:
                case BLUE_WOOL:

                    if (menu == null) {
                        menu = Bukkit.createInventory(null, 9, "Sélecteur d'équipes");
                        menu.setItem(3, new ItemBuilder(Material.BLUE_WOOL).setDisplayName("&1Équipe Bleue").toItemStack());
                        menu.setItem(4, new ItemBuilder(Material.WHITE_WOOL).setDisplayName("Pas d'équipe").toItemStack());
                        menu.setItem(5, new ItemBuilder(Material.RED_WOOL).setDisplayName("&cÉquipe Rouge").toItemStack());
                    }

                    player.openInventory(menu);
                    break;
    
                default: break;
            }

        }

    }

    @EventHandler
    public void clickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!getArena().isPlayerInArena(player)) return;

        if (event.getView().getTitle().contains("Sélecteur d'équipes")) {

            if (event.getCurrentItem() == null) return;
            if (event.getCurrentItem().getType() == null) return;

            switch (event.getCurrentItem().getType()) {
                case WHITE_WOOL:
                    getArena().getBlueTeam().removePlayer(player);
                    getArena().getRedTeam().removePlayer(player);

                    getArena().sendMessage(player.getDisplayName() + " est maintenant dans aucune équipe.");
                    player.sendTitle(ChatUtility.format("Vous êtes maintenant dans aucune équipe."), "", 10, 70, 20);

                    player.getInventory().getItemInMainHand().setType(Material.WHITE_WOOL);
                    player.closeInventory();
                    break;

                case RED_WOOL:
                    getArena().getBlueTeam().removePlayer(player);
                    getArena().getRedTeam().addPlayer(player);
    
                    getArena().sendMessage(player.getDisplayName() + " a rejoint l'&céquipe rouge&f.");
                    player.sendTitle(ChatUtility.format("Vous avez rejoint l'&céquipe rouge&f."), "", 10, 70, 20);
    
                    player.getInventory().getItemInMainHand().setType(Material.RED_WOOL);
                    player.closeInventory();
                    break;
    
                case BLUE_WOOL:

                    getArena().getRedTeam().removePlayer(player);
                    getArena().getBlueTeam().addPlayer(player);
    
                    getArena().sendMessage(player.getDisplayName() + " a rejoint l'&1équipe bleue&f.");
                    player.sendTitle(ChatUtility.format("Vous avez rejoint l'&1équipe bleue&f."), "", 10, 70, 20);
    
                    player.getInventory().getItemInMainHand().setType(Material.BLUE_WOOL);
                    player.closeInventory();
                    break;
    
                default: break;
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
        if (!(entity instanceof Player)) return;

        Player player = (Player) entity;
        if (!getArena().isPlayerInArena(player)) return;

        Item item = event.getItem();
        if (item.getItemStack().getType() != Material.WHITE_WOOL
        || item.getItemStack().getType() != Material.RED_WOOL
        || item.getItemStack().getType() != Material.BLUE_WOOL) event.setCancelled(true);
    }
    
}