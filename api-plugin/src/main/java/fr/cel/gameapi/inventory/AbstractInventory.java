package fr.cel.gameapi.inventory;

import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class AbstractInventory {

    private final String inventoryName;
    private final int size;
    private Inventory inv;

    public AbstractInventory(String inventoryName, int size) {
        this.inventoryName = inventoryName;
        this.size = size;
    }

    public void createInventory() {
        inv = Bukkit.createInventory(null, size, inventoryName);

        if (makeGlassPane()) {
            for (int i = 0; i < inv.getSize(); i++) {
                setItem(i, new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).hideTooltip().toItemStack());
            }
        }

        addItems(inv);
    }

    protected void setItem(int slot, ItemStack itemStack) {
        inv.setItem(slot, itemStack);
    }

    protected boolean makeGlassPane() {
        return true;
    }

    protected abstract void addItems(Inventory inv);

    public abstract void interact(Player player, String itemName, ItemStack item);

}