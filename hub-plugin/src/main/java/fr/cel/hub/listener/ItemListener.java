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
import org.bukkit.inventory.meta.ItemMeta;

public class ItemListener extends HListener {

    public ItemListener(Hub main) {
        super(main);
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;

        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (itemMeta.getDisplayName().equals("Sélectionneur de mini-jeux")) {
                GameAPI.getInstance().getInventoryManager().openInventory(new MinigamesInventory(), player);
            }

            else if (itemMeta.getDisplayName().equalsIgnoreCase("Mon Profil")) {
                GameAPI.getInstance().getInventoryManager().openInventory(new ProfileInventory(player), player);
            }
        }
    }

    @EventHandler
    public void onInteractInventory(final InventoryClickEvent event) {
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
    public void onSwappedItem(final PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;
        if (player.isOp()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(final PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) return;
        if (player.isOp()) return;

        ItemStack item = event.getItemDrop().getItemStack();
        if (item.getType() == Material.COMPASS || item.getType() == Material.PLAYER_HEAD) {
            event.setCancelled(true);
        }
    }

}