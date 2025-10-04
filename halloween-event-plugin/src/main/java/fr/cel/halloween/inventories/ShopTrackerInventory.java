package fr.cel.halloween.inventories;

import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.halloween.manager.GameManager;
import fr.cel.halloween.map.HalloweenMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopTrackerInventory extends AbstractInventory {

    private final HalloweenMap map;
    private final Location location;

    public ShopTrackerInventory(HalloweenMap map, Location location) {
        super(Component.text("Boutique - Traqueur"), 27);
        this.map = map;
        this.location = location;
    }

    @Override
    protected void addItems(Inventory inventory) {
        setItem(11, new ItemBuilder(Material.GOLD_INGOT).itemName(Component.text("Les impôts")).toItemStack());
        setItem(13, new ItemBuilder(Material.GHAST_TEAR).itemName(Component.text("Esprit fantomatique")).toItemStack());
        setItem(15, new ItemBuilder(Material.SKELETON_SKULL).itemName(Component.text("La blague du diable")).toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack itemStack) {
        switch (itemStack.getType()) {
            case GOLD_INGOT, GHAST_TEAR, SKELETON_SKULL -> {
                if (location != null) map.getActiveChests().remove(location);

                player.getInventory().addItem(itemStack);
                player.sendMessage(GameManager.getPrefix().append(Component.text("Vous avez pris " + itemName)));
                player.closeInventory();
            }

            default -> {}
        }
    }

}