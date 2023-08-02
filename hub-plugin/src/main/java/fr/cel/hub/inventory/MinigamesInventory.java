package fr.cel.hub.inventory;

import fr.cel.hub.Hub;
import fr.cel.hub.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MinigamesInventory extends AbstractInventory {

    private final Location locationMusee;

    public MinigamesInventory(Hub main) {
        super("Sélectionneur de mini-jeux", 54, main);
        locationMusee = new Location(Bukkit.getWorld("world"), 234.5, 95, 412.5, -90.0f, 0.0f);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(9, new ItemBuilder(Material.SPYGLASS).setDisplayName(Component.text("Cache-Cache", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(11, new ItemBuilder(Material.BOW).setDisplayName(Component.text("Valocraft", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(13, new ItemBuilder(Material.NETHERITE_SWORD).setDisplayName(Component.text("PVP", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(15, new ItemBuilder(Material.IRON_BOOTS).setDisplayName(Component.text("Parkour", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(17, new ItemBuilder(Material.BRUSH).setDisplayName(Component.text("Musée", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)).toItemStack());

        for (int slot = 27; slot <= 35; slot += 2) {
            inv.setItem(slot, new ItemBuilder(Material.COMMAND_BLOCK).setDisplayName(Component.text("Indisponible", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)).toItemStack());
        }

        inv.setItem(49, new ItemBuilder(Material.BARRIER).setDisplayName(Component.text("Quitter").decoration(TextDecoration.ITALIC, false)).toItemStack());
    }

    @Override
    protected void interact(Player player, Component itemName, Material type, Hub main) {
        switch (type) {
            case SPYGLASS -> player.openInventory(inventoryManager.getInventories().get("cachecache").getInv());

            case BOW -> player.openInventory(inventoryManager.getInventories().get("valocraft").getInv());

            case NETHERITE_SWORD -> player.openInventory(inventoryManager.getInventories().get("pvp").getInv());

            case IRON_BOOTS -> {
                // inventaire parkour
            }

            case BRUSH -> {
                player.teleport(locationMusee);
                player.closeInventory();
            }

            case COMMAND_BLOCK -> {
                sendMessageWithPrefix(player, "Indisponible pour le moment.");
                player.closeInventory();
            }

            case BARRIER -> player.closeInventory();

            default -> {}
        }
    }

}
