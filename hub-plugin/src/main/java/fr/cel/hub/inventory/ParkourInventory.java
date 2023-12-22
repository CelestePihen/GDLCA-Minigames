package fr.cel.hub.inventory;

import fr.cel.hub.Hub;
import fr.cel.hub.utils.ItemBuilder;
import fr.cel.parkour.manager.ParkourGameManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ParkourInventory extends AbstractInventory {

    public ParkourInventory(Hub main) {
        super("Parkour", 18, main);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.QUARTZ_BLOCK).setDisplayName("Parkour 1").toItemStack());

        inv.setItem(13, new ItemBuilder(Material.BARRIER).setDisplayName("Quitter").toItemStack());
    }

    @Override
    protected void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case QUARTZ_BLOCK -> ParkourGameManager.getGameManager().getMapManager().getArenaByDisplayName("Parkour 1").addPlayer(player);

            case BARRIER -> player.openInventory(inventoryManager.getInventory("minigames"));

            default -> { }
        }
    }

}