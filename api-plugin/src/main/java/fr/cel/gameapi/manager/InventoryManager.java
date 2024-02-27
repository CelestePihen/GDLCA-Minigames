package fr.cel.gameapi.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.listeners.InventoryListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class InventoryManager extends ManagerAPI {

    @Getter private Map<UUID, AbstractInventory> inventoryDataMap;

    public void enable() {
        this.inventoryDataMap = new HashMap<>();
        GameAPI.getInstance().registerEvents(new InventoryListener());
    }

    public void disable() {
        this.inventoryDataMap.forEach((uuid, abstractInventory) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.closeInventory();
            }
        });
    }

    public void openInventory(AbstractInventory inventory, final Player player) {
        inventory.createInventory();
        player.openInventory(inventory.getInv());
        getInventoryDataMap().put(player.getUniqueId(), inventory);
    }

}