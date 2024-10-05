package fr.cel.gameapi.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.listeners.InventoryListener;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class InventoryManager {

    private final Map<UUID, AbstractInventory> inventoryDataMap;

    public InventoryManager(GameAPI main) {
        this.inventoryDataMap = new HashMap<>();
        main.getServer().getPluginManager().registerEvents(new InventoryListener(this), main);
    }

    public void openInventory(AbstractInventory inventory, final Player player) {
        inventory.createInventory();
        player.openInventory(inventory.getInv());
        inventoryDataMap.put(player.getUniqueId(), inventory);
    }

}