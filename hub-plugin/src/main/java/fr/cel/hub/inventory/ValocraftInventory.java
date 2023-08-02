package fr.cel.hub.inventory;

import fr.cel.hub.Hub;
import fr.cel.hub.utils.ItemBuilder;
import fr.cel.valocraft.manager.ValoGameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ValocraftInventory extends AbstractInventory {

    public ValocraftInventory(Hub main) {
        super("Valocraft", 27, main);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(11, new ItemBuilder(Material.SAND).setDisplayName(Component.text("Désert", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(13, new ItemBuilder(Material.SNOW_BLOCK).setDisplayName(Component.text("Neige", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(15, new ItemBuilder(Material.CHERRY_LEAVES).setDisplayName(Component.text("Temple", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());

        inv.setItem(22, new ItemBuilder(Material.BARRIER).setDisplayName(Component.text("Retour").decoration(TextDecoration.ITALIC, false)).toItemStack());
    }

    @Override
    protected void interact(Player player, Component itemName, Material type, Hub main) {
        switch (type) {
            case SAND -> ValoGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Désert").addPlayer(player);
            case SNOW_BLOCK -> ValoGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Neige").addPlayer(player);
            case CHERRY_LEAVES -> ValoGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Temple").addPlayer(player);

            case BARRIER -> player.openInventory(inventoryManager.getInventories().get("minigames").getInv());

            default -> { }
        }
    }

}