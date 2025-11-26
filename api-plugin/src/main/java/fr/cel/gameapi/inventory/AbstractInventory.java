package fr.cel.gameapi.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.inventory.InventoryTypes;
import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class AbstractInventory {

    @NotNull private final Component inventoryName;
    private final int size;
    @Getter protected InventoryTypes type = InventoryTypes.GLOBAL;

    private Inventory inv;

    /**
     * Constructor for the abstract inventory
     * @param inventoryName The name of the inventory
     * @param size The size of the inventory
     */
    public AbstractInventory(@NotNull Component inventoryName, int size) {
        this.inventoryName = inventoryName.color(NamedTextColor.WHITE);

        if (size % 9 != 0 || size < 9 || size > 54) {
            throw new IllegalArgumentException("Inventory size must be a multiple of 9 and between 9 and 54.");
        }

        this.size = size;
    }

    public AbstractInventory(@NotNull Component inventoryName, @NotNull Rows rows) {
        this.inventoryName = inventoryName.color(NamedTextColor.WHITE);
        this.size = rows.getSize();
    }

    /**
     * Creates the inventory and adds items to it
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
     * Opens the inventory for the specified player
     * @param player The player to open the inventory for
     */
    public void open(@NotNull Player player) {
        GameAPI.getInstance().getInventoryManager().openInventory(this, player);
    }

    /**
     * Sets an item in the specified slot
     * @param slot The slot where you want to place the item
     * @param itemStack The ItemStack to set
     */
    protected void setItem(int slot, @Nullable ItemStack itemStack) {
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
    protected abstract void addItems(@NotNull Inventory inv);

    /**
     * Handles interaction with the inventory
     * This method must be implemented in classes extending this abstract class
     * @param player The player interacting with the inventory
     * @param itemName The name of the item clicked
     * @param item The ItemStack that was clicked
     */
    public abstract void interact(@NotNull Player player, @NotNull String itemName, @NotNull ItemStack item);

    public String getGlobalKey() {
        return getClass().getSimpleName();
    }

    public enum Rows {
        ONE(9),
        TWO(18),
        THREE(27),
        FOUR(36),
        FIVE(45),
        SIX(54);

        @Getter private final int size;

        Rows(int size) {
            this.size = size;
        }
    }

}