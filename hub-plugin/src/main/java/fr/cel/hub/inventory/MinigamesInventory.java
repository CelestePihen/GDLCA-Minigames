package fr.cel.hub.inventory;

import fr.cel.hub.Hub;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.hub.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MinigamesInventory extends AbstractInventory {

    private final Location locationMusee;

    public MinigamesInventory(Hub main) {
        super("Sélectionneur de mini-jeux", 54, main);
        locationMusee = new Location(Bukkit.getWorld("world"), 234.5, 95, 412.5, -90.0f, 0.0f);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(9, new ItemBuilder(Material.SPYGLASS).setDisplayName(ChatUtility.format("&aCache-Cache")).toItemStack());
        inv.setItem(11, new ItemBuilder(Material.BOW).setDisplayName(ChatUtility.format("&aValocraft")).toItemStack());
        inv.setItem(13, new ItemBuilder(Material.NETHERITE_SWORD).setDisplayName(ChatUtility.format("&aPVP")).toItemStack());
        inv.setItem(15, new ItemBuilder(Material.IRON_BOOTS).setDisplayName(ChatUtility.format("&aParkour")).toItemStack());
        inv.setItem(17, new ItemBuilder(Material.BRUSH).setDisplayName(ChatUtility.format("&aMusée")).toItemStack());

        for (int slot = 27; slot <= 35; slot += 2) {
            inv.setItem(slot, new ItemBuilder(Material.COMMAND_BLOCK).setDisplayName(ChatUtility.format("&eIndisponible")).toItemStack());
        }

        inv.setItem(49, new ItemBuilder(Material.BARRIER).setDisplayName("Quitter").toItemStack());
    }

    @Override
    protected void interact(Player player, String itemName, ItemStack item, Hub main) {
        switch (item.getType()) {
            case SPYGLASS -> player.openInventory(inventoryManager.getInventories().get("cachecache").getInv());

            case BOW -> player.openInventory(inventoryManager.getInventories().get("valocraft").getInv());

            case NETHERITE_SWORD -> player.openInventory(inventoryManager.getInventories().get("pvp").getInv());

            case IRON_BOOTS -> player.sendMessage("Parkour pas encore fait");

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
