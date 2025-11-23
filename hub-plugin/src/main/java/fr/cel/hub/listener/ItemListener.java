package fr.cel.hub.listener;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.CosmeticsInventory;
import fr.cel.gameapi.inventory.ProfileInventory;
import fr.cel.hub.Hub;
import fr.cel.hub.inventory.MinigamesInventory;
import net.kyori.adventure.text.TextComponent;
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
        final Player player = event.getPlayer();

        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)
                || event.getHand() != EquipmentSlot.HAND) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            String itemName = itemStack.getItemMeta().hasItemName() ?
                    ((TextComponent) itemStack.getItemMeta().itemName()).content() : "";

            String displayName = itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().displayName() != null ?
                    ((TextComponent) itemStack.getItemMeta().displayName()).content() : "";

            if (itemStack.getType() == Material.ARMOR_STAND && itemName.equalsIgnoreCase("Cosmétiques")) {
                GameAPI.getInstance().getInventoryManager().openInventory(new CosmeticsInventory(player), player);
                event.setCancelled(true);
            }

            else if (itemStack.getType() == Material.COMPASS && itemName.equalsIgnoreCase("Sélectionneur de mini-jeux")) {
                GameAPI.getInstance().getInventoryManager().openInventory(new MinigamesInventory(), player);
                event.setCancelled(true);
            }

            else if (itemStack.getType() == Material.PLAYER_HEAD && displayName.equalsIgnoreCase("Mon Profil")) {
                GameAPI.getInstance().getInventoryManager().openInventory(new ProfileInventory(player), player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player) || player.isOp()) return;

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        if (item.getType() == Material.COMPASS || item.getType() == Material.PLAYER_HEAD) event.setCancelled(true);
    }

    @EventHandler
    public void onSwappedItem(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player) || player.isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player) || player.isOp()) return;
        event.setCancelled(true);
    }

}