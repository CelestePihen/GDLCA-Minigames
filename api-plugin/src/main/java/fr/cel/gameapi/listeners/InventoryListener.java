package fr.cel.gameapi.listeners;

import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.manager.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InventoryListener implements Listener {

    private final InventoryManager inventoryManager;

    public InventoryListener(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        AbstractInventory abstractInventory = inventoryManager.getInventoryDataMap().getOrDefault(player.getUniqueId(), null);
        if (abstractInventory != null) {
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            if (item.getItemMeta() == null) return;

            abstractInventory.interact(player, item.getItemMeta().getDisplayName(), item);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();

        if (inventoryManager.getInventoryDataMap().getOrDefault(playerUUID, null) != null) {
            inventoryManager.getInventoryDataMap().remove(playerUUID);
        }
    }

}