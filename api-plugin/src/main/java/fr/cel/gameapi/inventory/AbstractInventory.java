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

    /**
     * Constructeur de l'inventaire abstrait
     * @param inventoryName Le nom de l'inventaire
     * @param size La taille de l'inventaire
     */
    public AbstractInventory(String inventoryName, int size) {
        this.inventoryName = inventoryName;
        this.size = size;
    }

    /**
     * Permet de créer l'inventaire
     */
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
     * Permet d'ajouter les items dans l'inventaire
     * Cette méthode doit être implémentée dans les classes qui étendent cette classe abstraite
     * @param inv L'inventaire dans lequel vous voulez ajouter les items
     */
    protected abstract void addItems(Inventory inv);

    /**
     * Permet de gérer l'interaction avec l'inventaire
     * Cette méthode doit être implémentée dans les classes qui étendent cette classe abstraite
     * @param player Le joueur qui interagit avec l'inventaire
     * @param itemName Le nom de l'item sur lequel le joueur a cliqué
     * @param item L'item sur lequel le joueur a cliqué
     */
    public abstract void interact(Player player, String itemName, ItemStack item);

}