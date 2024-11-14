package fr.cel.hub.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.halloween.manager.HalloweenMapManager;
import fr.cel.hub.Hub;
import fr.cel.hub.inventory.cachecache.CacheCacheInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MinigamesInventory extends AbstractInventory {

    private final Location locationMuseum = new Location(Bukkit.getWorld("world"), 234.5, 95, 412.5, -90.0f, 0.0f);

    public MinigamesInventory() {
        super("Sélectionneur de mini-jeux", 54);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(9, new ItemBuilder(Material.SPYGLASS).setDisplayName("&aCache-Cache").toItemStack());
        inv.setItem(11, new ItemBuilder(Material.BOW).setDisplayName("&aValocraft").toItemStack());
        inv.setItem(13, new ItemBuilder(Material.NETHERITE_SWORD).setDisplayName("&aPVP").toItemStack());
        inv.setItem(15, new ItemBuilder(Material.IRON_BOOTS).setDisplayName("&aParkour").toItemStack());
        inv.setItem(17, new ItemBuilder(Material.BRUSH).setDisplayName("&aMusée").toItemStack());

        // Event Halloween
        if (Hub.getInstance().getConfig().getBoolean("halloweenEvent")) {
            for (int slot = 27; slot <= 35; slot += 2) {
                inv.setItem(slot, new ItemBuilder(Material.JACK_O_LANTERN).setDisplayName("&6Halloween").toItemStack());
            }
        }

        // Event Noël
        else if (Hub.getInstance().getConfig().getBoolean("christmasEvent")) {
            for (int slot = 27; slot <= 35; slot += 2) {
                inv.setItem(slot, new ItemBuilder(Material.SNOW_BLOCK).setDisplayName("&6Noël").toItemStack());
            }
        }

        // Indisponible
        else {
            for (int slot = 27; slot <= 35; slot += 2) {
                inv.setItem(slot, new ItemBuilder(Material.COMMAND_BLOCK).setDisplayName("&eIndisponible").toItemStack());
            }
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

            case JACK_O_LANTERN -> HalloweenMapManager.getMapManager().getMaps().get("manoir").addPlayer(player);

            case SNOW_BLOCK -> player.sendMessage(GameAPI.getPrefix() + "Cet événement n'est pas encore prêt...");
            /*ChristmasMapManager.getMapManager().getMaps().get("atelier").addPlayer(player);*/

            case COMMAND_BLOCK -> {
                player.sendMessage(GameAPI.getPrefix() + "Indisponible pour le moment.");
                player.closeInventory();
            }

            case BARRIER -> player.closeInventory();

            default -> {}
        }
    }

}