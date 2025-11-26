package fr.cel.gameapi.manager;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.listeners.InventoryListener;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class InventoryManager {

    @Deprecated(forRemoval = true)
    private final Map<UUID, AbstractInventory> inventoryDataMap = new HashMap<>();

//    private final Map<String, AbstractInventory> globalInventories = new HashMap<>();

    public InventoryManager(GameAPI main) {
        main.getServer().getPluginManager().registerEvents(new InventoryListener(this), main);
    }

    /**
     * Opens a new AbstractInventory for a player
     * @param inventory The AbstractInventory instance to open
     * @param player The player to show the inventory to
     */
    @Deprecated(forRemoval = true)
    public void openInventory(@NotNull AbstractInventory inventory, @NotNull Player player) {
        inventory.createInventory();
        player.openInventory(inventory.getInv());
        inventoryDataMap.put(player.getUniqueId(), inventory);
    }

//    /**
//     * Opens an inventory for a player
//     * @param inventory The AbstractInventory instance to open
//     * @param player The player to show the inventory to
//     */
//    public void openInventory(@NotNull AbstractInventory inventory, @NotNull Player player) {
//        if (inventory.getType() == InventoryType.GLOBAL) {
//            // Cache et réutilise l'inventaire global
//            AbstractInventory cached = globalInventories.computeIfAbsent(
//                    inventory.getGlobalKey(),
//                    key -> {
//                        inventory.createInventory();
//                        return inventory;
//                    }
//            );
//            player.openInventory(cached.getInv());
//        } else {
//            // Crée un nouvel inventaire personnel à chaque ouverture
//            inventory.createInventory();
//            player.openInventory(inventory.getInv());
//        }
//    }

}