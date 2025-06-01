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

    private final Map<UUID, AbstractInventory> inventoryDataMap = new HashMap<>();

    public InventoryManager(GameAPI main) {
        main.getServer().getPluginManager().registerEvents(new InventoryListener(this), main);
    }

    /**
     * Montre un nouvelle inventaire de type AbstractInventory à un joueur
     * @param inventory L'inventaire de type AbstractInventory
     * @param player Le joueur à qui montrer l'inventaire
     */
    public void openInventory(AbstractInventory inventory, Player player) {
        if (inventory == null || player == null) {
            return;
        }

        inventory.createInventory();
        player.openInventory(inventory.getInv());
        inventoryDataMap.put(player.getUniqueId(), inventory);
    }

}