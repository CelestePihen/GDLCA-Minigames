package fr.cel.decorationsplugin.manager;

import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a decoration in the game.
 * Each decoration has a name, display name, and dimensions.
 */
public record Decoration(String name, String displayName, int sizeX, int sizeY, int sizeZ, boolean sittable) {

    public ItemStack getDisplayItem() {
        return new ItemBuilder(Material.PAPER).itemName(Component.text(displayName())).setItemModel(name()).toItemStack();
    }

}