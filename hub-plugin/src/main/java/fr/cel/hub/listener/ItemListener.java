package fr.cel.hub.listener;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.ProfileInventory;
import fr.cel.hub.Hub;
import fr.cel.hub.inventory.MinigamesInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemListener extends HListener {

    public ItemListener(Hub main) {
        super(main);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;

        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (itemStack.getType() == Material.COMPASS) {
                GameAPI.getInstance().getInventoryManager().openInventory(new MinigamesInventory(), player);
            }

            else if (itemStack.getType() == Material.PLAYER_HEAD) {
                GameAPI.getInstance().getInventoryManager().openInventory(new ProfileInventory(player), player);
            }
        }
    }

    @EventHandler
    public void onInteractInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;
        if (player.isOp()) return;

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        if (item.getType() == Material.COMPASS || item.getType() == Material.PLAYER_HEAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwappedItem(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;
        if (player.isOp()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;
        if (player.isOp()) return;

        ItemStack item = event.getItemDrop().getItemStack();
        if (item.getType() == Material.COMPASS || item.getType() == Material.PLAYER_HEAD) {
            event.setCancelled(true);
        }
    }

}