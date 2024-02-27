package fr.cel.gameapi.listeners;

import fr.cel.gameapi.GameAPI;
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

    private InventoryManager getInventoryManager() {
        return GameAPI.getInstance().getInventoryManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        AbstractInventory abstractInventory = getInventoryManager().getInventoryDataMap().getOrDefault(player.getUniqueId(), null);
        if (abstractInventory != null) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            if (item.getItemMeta() == null) return;

            abstractInventory.interact(player, item.getItemMeta().getDisplayName(), item);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        AbstractInventory abstractInventory = getInventoryManager().getInventoryDataMap().getOrDefault(playerUUID, null);
        if (abstractInventory != null) {
            getInventoryManager().getInventoryDataMap().remove(playerUUID);
        }
    }

}