package fr.cel.gameapi.inventory;

import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class AbstractInventory {

    private final Component inventoryName;
    private final int size;
    private Inventory inv;

    /**
     * Constructor for the abstract inventory
     * @param inventoryName The name of the inventory
     * @param size The size of the inventory
     */
    public AbstractInventory(Component inventoryName, int size) {
        this.inventoryName = inventoryName.color(NamedTextColor.WHITE);
        this.size = size;
    }

    /**
     * Creates the inventory
     */
    public void createInventory() {
        inv = Bukkit.createInventory(null, size, inventoryName);

        addItems(inv);

        if (makeGlassPane()) {
            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack current = inv.getItem(i);
                if (current == null || current.getType() == Material.AIR) {
                    setItem(i, new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).hideTooltip().toItemStack());
                }
            }
        }
    }

    /**
     * Sets an item in the specified slot
     * @param slot The slot where you want to place the item
     * @param itemStack The ItemStack to set
     */
    protected void setItem(int slot, ItemStack itemStack) {
        inv.setItem(slot, itemStack);
    }

    /**
     * Determines whether to add glass panes to the inventory
     * @return True if glass panes should be added, false otherwise
     */
    protected boolean makeGlassPane() {
        return true;
    }

    /**
     * Adds items to the inventory
     * This method must be implemented in classes extending this abstract class
     * @param inv The inventory to add items to
     */
    protected abstract void addItems(Inventory inv);

    /**
     * Handles interaction with the inventory
     * This method must be implemented in classes extending this abstract class
     * @param player The player interacting with the inventory
     * @param itemName The name of the item clicked
     * @param item The ItemStack that was clicked
     */
    public abstract void interact(Player player, String itemName, ItemStack item);

}