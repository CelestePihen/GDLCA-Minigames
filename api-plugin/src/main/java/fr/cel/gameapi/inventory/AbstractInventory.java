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

    /**
     * Permet de mettre un item au slot spécifié
     * @param slot Le slot où vous voulez que l'item va
     * @param itemStack L'itemstack de l'item
     */
    protected void setItem(int slot, ItemStack itemStack) {
        inv.setItem(slot, itemStack);
    }

    /**
     * Permet d'ajouter ou pas des vitres dans l'inventaire
     * @return Vrai si vous en voulez, faux si vous n'en voulez pas
     */
    protected boolean makeGlassPane() {
        return true;
    }

    /**
     * Permet d'ajouter des items à la création de l'inventaire
     * @param inv L'inventaire
     */
    protected abstract void addItems(Inventory inv);

    /**
     * Permet de spécifier quel item fait quoi quand on le sélectionne dans l'inventaire
     * @param player Le joueur qui a effectué l'action
     * @param itemName Le nom de l'item
     * @param item L'item
     */
    public abstract void interact(Player player, String itemName, ItemStack item);

}