package fr.cel.hub.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.hub.Hub;
import fr.cel.hub.inventory.cachecache.CacheCacheInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class MinigamesInventory extends AbstractInventory {

    private static final Location LOCATION_MUSEUM = new Location(Bukkit.getWorld("world"), 234.5, 95, 412.5, -90.0f, 0.0f);

    public MinigamesInventory() {
        super(Component.text("Sélectionneur de mini-jeux"), 54);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(9, new ItemBuilder(Material.SPYGLASS).itemName(Component.text("Cache-Cache", NamedTextColor.GREEN)).toItemStack());
        inv.setItem(11, new ItemBuilder(Material.BOW).itemName(Component.text("Valocraft", NamedTextColor.GREEN)).toItemStack());
        inv.setItem(13, new ItemBuilder(Material.NETHERITE_SWORD).itemName(Component.text("PVP", NamedTextColor.GREEN)).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).toItemStack());
        inv.setItem(15, new ItemBuilder(Material.IRON_BOOTS).itemName(Component.text("Parkour", NamedTextColor.GREEN)).toItemStack());
        inv.setItem(17, new ItemBuilder(Material.BRUSH).itemName(Component.text("Musée", NamedTextColor.GREEN)).toItemStack());

        // Event Halloween
        if (Hub.getInstance().getConfig().getBoolean("halloweenEvent")) {
            for (int slot = 27; slot <= 35; slot += 2) {
                inv.setItem(slot, new ItemBuilder(Material.JACK_O_LANTERN).itemName(Component.text("Halloween", NamedTextColor.GOLD)).toItemStack());
            }
        }

        // Event Noël
        else if (Hub.getInstance().getConfig().getBoolean("christmasEvent")) {
            for (int slot = 27; slot <= 35; slot += 2) {
                inv.setItem(slot, new ItemBuilder(Material.SNOW_BLOCK).itemName(Component.text("Noël", NamedTextColor.GOLD)).toItemStack());
            }
        }

        // Indisponible
        else {
            for (int slot = 27; slot <= 35; slot += 2) {
                inv.setItem(slot, new ItemBuilder(Material.COMMAND_BLOCK).itemName(Component.text("Indisponible", NamedTextColor.YELLOW)).toItemStack());
            }
        }

        inv.setItem(49, new ItemBuilder(Material.BARRIER).itemName(Component.text("Quitter", NamedTextColor.RED)).toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case SPYGLASS -> GameAPI.getInstance().getInventoryManager().openInventory(new CacheCacheInventory(), player);

            case BOW -> GameAPI.getInstance().getInventoryManager().openInventory(new ValocraftInventory(), player);

            case NETHERITE_SWORD -> GameAPI.getInstance().getInventoryManager().openInventory(new PVPInventory(), player);

            case IRON_BOOTS -> GameAPI.getInstance().getInventoryManager().openInventory(new ParkourInventory(), player);

            case BRUSH -> {
                player.closeInventory();
                player.teleport(LOCATION_MUSEUM);
            }

            case JACK_O_LANTERN -> {
//                player.closeInventory();
//                HalloweenMapManager.getMapManager().getMaps().get("manoir").addPlayer(player);
                player.sendMessage(GameAPI.getPrefix().append(Component.text("Cet événement n'est pas encore prêt...")));
            }

            case SNOW_BLOCK -> player.sendMessage(GameAPI.getPrefix().append(Component.text("Cet événement n'est pas encore prêt...")));
            /*ChristmasMapManager.getMapManager().getMaps().get("atelier").addPlayer(player);*/

            case COMMAND_BLOCK -> player.sendMessage(GameAPI.getPrefix().append(Component.text("Indisponible pour le moment.")));

            case BARRIER -> player.closeInventory();

            default -> {}
        }
    }

}