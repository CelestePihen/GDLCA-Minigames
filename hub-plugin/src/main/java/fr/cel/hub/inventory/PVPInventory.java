package fr.cel.hub.inventory;

import fr.cel.cachecache.manager.CCGameManager;
import fr.cel.hub.Hub;
import fr.cel.hub.utils.ItemBuilder;
import fr.cel.pvp.manager.PVPGameManager;
import fr.cel.valocraft.manager.ValoGameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PVPInventory extends AbstractInventory {

    public PVPInventory(Hub main) {
        super("PVP", 27, main);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(12, new ItemBuilder(Material.AMETHYST_BLOCK).setDisplayName(Component.text("Alpha", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(14, new ItemBuilder(Material.STONE_BRICKS).setDisplayName(Component.text("Beta", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());

        inv.setItem(22, new ItemBuilder(Material.BARRIER).setDisplayName(Component.text("Retour").decoration(TextDecoration.ITALIC, false)).toItemStack());
    }

    @Override
    protected void interact(Player player, Component itemName, Material type, Hub main) {
        switch (type) {
            case AMETHYST_BLOCK -> PVPGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Alpha").addPlayer(player);
            case STONE_BRICKS -> PVPGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Beta").addPlayer(player);

            case BARRIER -> player.openInventory(inventoryManager.getInventories().get("minigames").getInv());

            default -> { }
        }
    }
}
