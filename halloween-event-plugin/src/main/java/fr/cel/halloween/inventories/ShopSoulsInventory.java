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

public class ShopSoulsInventory extends AbstractInventory {

    private final HalloweenMap map;
    private final Location location;

    public ShopSoulsInventory(HalloweenMap map, Location location) {
        super(Component.text("Boutique - Âmes errantes"), 27);
        this.map = map;
        this.location = location;
    }

    public ShopSoulsInventory(HalloweenMap map) {
        this(map, null);
    }

    @Override
    protected void addItems(Inventory inventory) {
        setItem(11, new ItemBuilder(Material.POTION).itemName(Component.text("Et pouf")).toItemStack());
        setItem(13, new ItemBuilder(Material.GOLD_NUGGET).itemName(Component.text("ARGENT !!!")).toItemStack());
        setItem(15, new ItemBuilder(Material.CHEST).itemName(Component.text("Marché noir")).toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack itemStack) {
        switch (itemStack.getType()) {
            case POTION, GOLD_NUGGET, CHEST -> {
                if (location != null) map.getActiveChests().remove(location);

                player.getInventory().addItem(itemStack);
                player.sendMessage(GameManager.getPrefix().append(Component.text("Vous avez pris " + itemName)));
                player.closeInventory();
            }

            default -> {}
        }
    }

}