package fr.cel.gameapi.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.statistics.*;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StatisticsInventory extends AbstractInventory {

    private final Player player;

    public StatisticsInventory(Player player) {
        super(Component.text("Statistiques"), 27);
        this.player = player;
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(9, new ItemBuilder(Material.COMPASS).itemName(Component.text("Hub", NamedTextColor.GREEN)).toItemStack());
        inv.setItem(11, new ItemBuilder(Material.SPYGLASS).itemName(Component.text("Cache-Cache", NamedTextColor.GREEN)).toItemStack());
        inv.setItem(13, new ItemBuilder(Material.BOW).itemName(Component.text("Valocraft", NamedTextColor.GREEN)).toItemStack());
        inv.setItem(15, new ItemBuilder(Material.NETHERITE_SWORD).itemName(Component.text("PVP", NamedTextColor.GREEN)).toItemStack());
        inv.setItem(17, new ItemBuilder(Material.IRON_BOOTS).itemName(Component.text("Parkour", NamedTextColor.GREEN)).toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case COMPASS -> GameAPI.getInstance().getInventoryManager().openInventory(new HubStatsInventory(this.player), player);

            case SPYGLASS -> GameAPI.getInstance().getInventoryManager().openInventory(new CCStatsInventory(this.player), player);

            case BOW -> GameAPI.getInstance().getInventoryManager().openInventory(new ValoStatsInventory(this.player), player);

            case NETHERITE_SWORD -> GameAPI.getInstance().getInventoryManager().openInventory(new PVPStatsInventory(this.player), player);

            case IRON_BOOTS -> GameAPI.getInstance().getInventoryManager().openInventory(new ParkourStatsInventory(this.player), player);

            default -> {}
        }
    }

}