package fr.cel.hub.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.hub.Hub;
import fr.cel.hub.inventory.cachecache.CacheCacheInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MinigamesInventory extends AbstractInventory {

    private final Location locationMuseum;

    public MinigamesInventory() {
        super("Sélectionneur de mini-jeux", 54);
        locationMuseum = new Location(Bukkit.getWorld("world"), 234.5, 95, 412.5, -90.0f, 0.0f);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(9, new ItemBuilder(Material.SPYGLASS).setDisplayName("&aCache-Cache").toItemStack());
        inv.setItem(11, new ItemBuilder(Material.BOW).setDisplayName("&aValocraft").toItemStack());
        inv.setItem(13, new ItemBuilder(Material.NETHERITE_SWORD).setDisplayName("&aPVP").toItemStack());
        inv.setItem(15, new ItemBuilder(Material.IRON_BOOTS).setDisplayName("&aParkour").toItemStack());
        inv.setItem(17, new ItemBuilder(Material.BRUSH).setDisplayName("&aMusée").toItemStack());

        for (int slot = 27; slot <= 35; slot += 2) {
            inv.setItem(slot, new ItemBuilder(Material.COMMAND_BLOCK).setDisplayName("&eIndisponible").toItemStack());
        }

        inv.setItem(49, new ItemBuilder(Material.BARRIER).setDisplayName("Quitter").toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case SPYGLASS -> GameAPI.getInstance().getInventoryManager().openInventory(new CacheCacheInventory(), player);

            case BOW -> GameAPI.getInstance().getInventoryManager().openInventory(new ValocraftInventory(), player);

            case NETHERITE_SWORD -> GameAPI.getInstance().getInventoryManager().openInventory(new PVPInventory(), player);

            case IRON_BOOTS -> GameAPI.getInstance().getInventoryManager().openInventory(new ParkourInventory(), player);

            case BRUSH -> player.teleport(locationMuseum);

            case COMMAND_BLOCK -> {
                player.sendMessage(GameAPI.getInstance().getPrefix() + "Indisponible pour le moment.");
                player.closeInventory();
            }

            case BARRIER -> player.closeInventory();

            default -> {}
        }
    }

}