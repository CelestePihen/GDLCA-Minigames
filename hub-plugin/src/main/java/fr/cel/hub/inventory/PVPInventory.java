package fr.cel.hub.inventory;

import fr.cel.hub.Hub;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.hub.utils.ItemBuilder;
import fr.cel.pvp.manager.PVPGameManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PVPInventory extends AbstractInventory {

    public PVPInventory(Hub main) {
        super("PVP", 27, main);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(12, new ItemBuilder(Material.AMETHYST_BLOCK).setDisplayName("&dAlpha").toItemStack());
        inv.setItem(14, new ItemBuilder(Material.STONE_BRICKS).setDisplayName("&dBeta").toItemStack());

        inv.setItem(22, new ItemBuilder(Material.BARRIER).setDisplayName("Retour").toItemStack());
    }

    @Override
    protected void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case AMETHYST_BLOCK -> PVPGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Alpha").addPlayer(player);
            case STONE_BRICKS -> PVPGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Beta").addPlayer(player);

            case BARRIER -> player.openInventory(inventoryManager.getInventory("minigames"));

            default -> { }
        }
    }
}
