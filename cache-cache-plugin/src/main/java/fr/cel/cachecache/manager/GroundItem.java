package fr.cel.cachecache.manager;

import fr.cel.cachecache.arena.CCArena;
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
        this.itemStack = new ItemBuilder(material).setDisplayName(displayName).setItemModel(itemModel).setLore(lores).toItemStack();
    }

    /**
     * Cette méthode va s'activer quand le joueur fera clic droit avec l'objet
     * @param player L'instance du joueur
     * @param arena La carte où le joueur est
     */
    public void onInteract(Player player, CCArena arena) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getAmount() == 1) player.getInventory().setItemInMainHand(null);
        else itemInHand.setAmount(itemInHand.getAmount() - 1);
    }

}