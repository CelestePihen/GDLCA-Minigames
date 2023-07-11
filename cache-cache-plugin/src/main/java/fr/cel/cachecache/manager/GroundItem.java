package fr.cel.cachecache.manager;

import java.util.List;

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

    public GroundItem(String name, Material material, String displayName, List<String> lores) {
        this.name = name;
        this.material = material;
        this.displayName = displayName;
        this.lores = lores;
    }

    public ItemStack getItemStack() {
        return new ItemBuilder(material).setDisplayName(displayName).setLore(lores).toItemStack();
    }

    // L'action à effectuer
    public abstract void onInteract(Player player, Arena arena);

}