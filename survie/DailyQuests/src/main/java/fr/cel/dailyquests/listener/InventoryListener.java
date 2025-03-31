package fr.cel.dailyquests.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public final class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST) return;

        String title = event.getView().getTitle();
        if (title.equalsIgnoreCase("Vos quêtes")) {
            event.setCancelled(true);
        }
    }

}