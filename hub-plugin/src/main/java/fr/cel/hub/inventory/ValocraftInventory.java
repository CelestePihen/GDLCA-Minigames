package fr.cel.hub.inventory;

import fr.cel.hub.Hub;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.hub.utils.ItemBuilder;
import fr.cel.valocraft.manager.ValoGameManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ValocraftInventory extends AbstractInventory {

    public ValocraftInventory(Hub main) {
        super("Valocraft", 27, main);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(11, new ItemBuilder(Material.SAND).setDisplayName("&dDésert").toItemStack());
        inv.setItem(13, new ItemBuilder(Material.SNOW_BLOCK).setDisplayName("&dNeige").toItemStack());
        inv.setItem(15, new ItemBuilder(Material.CHERRY_LEAVES).setDisplayName("&dTemple").toItemStack());

        inv.setItem(22, new ItemBuilder(Material.BARRIER).setDisplayName("Retour").toItemStack());
    }

    @Override
    protected void interact(Player player, String itemName, ItemStack item, Hub main) {
        switch (item.getType()) {
            case SAND -> ValoGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Désert").addPlayer(player);
            case SNOW_BLOCK -> ValoGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Neige").addPlayer(player);
            case CHERRY_LEAVES -> ValoGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Temple").addPlayer(player);

            case BARRIER -> player.openInventory(inventoryManager.getInventories().get("minigames").getInv());

            default -> { }
        }
    }

}