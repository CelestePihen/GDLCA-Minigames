package fr.cel.cachecache.manager;

import java.util.List;

import fr.cel.cachecache.manager.arena.CCArena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.cel.hub.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
public abstract class GroundItem {

    private final String name;
    private final String displayName;
    private final ItemStack itemStack;

    public GroundItem(String name, Material material, String displayName, List<String> lores, int customModelData) {
        this.name = name;
        this.displayName = displayName;
        this.itemStack = new ItemBuilder(material).setDisplayName(displayName).setCustomModelData(customModelData).setLore(lores).toItemStack();
    }

    /**
     * L'action à effectuer
     * @param player Le joueur
     * @param arena L'arène où le joueur est
     */
    public void onInteract(Player player, CCArena arena) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getAmount() == 1) player.getInventory().setItemInMainHand(null);
        else itemInHand.setAmount(itemInHand.getAmount() - 1);
    }

}