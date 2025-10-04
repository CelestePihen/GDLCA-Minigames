package fr.cel.hub.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.valocraft.manager.ValoArenaManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ValocraftInventory extends AbstractInventory {

    public ValocraftInventory() {
        super(Component.text("Valocraft"), 27);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(10, new ItemBuilder(Material.SAND).itemName(Component.text("Désert", NamedTextColor.LIGHT_PURPLE)).toItemStack());
        inv.setItem(12, new ItemBuilder(Material.SNOW_BLOCK).itemName(Component.text("Neige", NamedTextColor.LIGHT_PURPLE)).toItemStack());
        inv.setItem(14, new ItemBuilder(Material.CHERRY_LEAVES).itemName(Component.text("Temple", NamedTextColor.LIGHT_PURPLE)).toItemStack());
        inv.setItem(16, new ItemBuilder(Material.ANDESITE).itemName(Component.text("Complex", NamedTextColor.LIGHT_PURPLE)).toItemStack());

        inv.setItem(22, new ItemBuilder(Material.BARRIER).itemName(Component.text("Retour", NamedTextColor.RED)).toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case SAND -> ValoArenaManager.getArenaManager().getArenaByDisplayName("Désert").addPlayer(player);
            case SNOW_BLOCK -> ValoArenaManager.getArenaManager().getArenaByDisplayName("Neige").addPlayer(player);
            case CHERRY_LEAVES -> ValoArenaManager.getArenaManager().getArenaByDisplayName("Temple").addPlayer(player);
            case ANDESITE -> ValoArenaManager.getArenaManager().getArenaByDisplayName("Complex").addPlayer(player);

            case BARRIER -> GameAPI.getInstance().getInventoryManager().openInventory(new MinigamesInventory(), player);

            default -> { }
        }
    }

}