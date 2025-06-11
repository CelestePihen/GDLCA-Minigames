package fr.cel.decorationsplugin.manager;

import fr.cel.gameapi.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a decoration in the game.
 * Each decoration has a name, display name, and dimensions.
 */
public record Decoration(String name, String displayName, int sizeX, int sizeY, int sizeZ) {

    public ItemStack getDisplayItem() {
        return new ItemBuilder(Material.PAPER).setItemName(displayName()).setItemModel(name()).toItemStack();
    }

}