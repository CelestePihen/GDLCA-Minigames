package fr.cel.hub.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.parkour.manager.ParkourMapManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ParkourInventory extends AbstractInventory {

    public ParkourInventory() {
        super("Parkour", 18);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(4, new ItemBuilder(Material.QUARTZ_BLOCK).setItemName("Parkour 1").toItemStack());

        inv.setItem(13, new ItemBuilder(Material.BARRIER).setItemName("Quitter").toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case QUARTZ_BLOCK -> ParkourMapManager.getMapManager().getMapByDisplayName("Parkour 1").addPlayer(player);

            case BARRIER -> GameAPI.getInstance().getInventoryManager().openInventory(new MinigamesInventory(), player);

            default -> { }
        }
    }

}