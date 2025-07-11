package fr.cel.cachecache.manager;

import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public abstract class GroundItem {

    private final String name;
    private final String displayName;
    private final ItemStack itemStack;

    public GroundItem(String name, Material material, String displayName, List<String> lores, String itemModel) {
        this.name = name;
        this.displayName = displayName;
        this.itemStack = new ItemBuilder(material).setItemName(displayName).setItemModel(itemModel).setLore(lores).toItemStack();
    }

    /**
     * Cette méthode va s'activer quand le joueur fera clic droit avec l'objet
     * @param player L'instance du joueur
     * @param map La carte où le joueur est
     */
    public void onInteract(Player player, CCMap map) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        itemInHand.setAmount(itemInHand.getAmount() - 1);
    }

}