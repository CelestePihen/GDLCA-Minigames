package fr.cel.cachecache.manager;

import java.util.List;

import fr.cel.cachecache.manager.arena.CCArena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.cel.hub.utils.ItemBuilder;
import lombok.Getter;

public abstract class GroundItem {

    @Getter private final String name;
    private final Material material;
    @Getter private final String displayName;
    private final List<String> lores;
    private final int customModelData;

    public GroundItem(String name, Material material, String displayName, List<String> lores, int customModelData) {
        this.name = name;
        this.material = material;
        this.displayName = displayName;
        this.lores = lores;
        this.customModelData = customModelData;
    }

    public ItemStack getItemStack() {
        return new ItemBuilder(material).setDisplayName(displayName).setCustomModelData(customModelData).setLore(lores).toItemStack();
    }

    /**
     * L'action à effectuer
     * @param player Le joueur
     * @param arena L'arène où le joueur est
     */
    public abstract void onInteract(Player player, CCArena arena);

}