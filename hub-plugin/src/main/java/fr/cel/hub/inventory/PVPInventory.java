package fr.cel.hub.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import fr.cel.hub.inventory.cachecache.CacheCacheInventory;
import fr.cel.pvp.manager.PVPGameManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PVPInventory extends AbstractInventory {

    public PVPInventory() {
        super("PVP", 27);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(12, new ItemBuilder(Material.AMETHYST_BLOCK).setDisplayName("&dAlpha").toItemStack());
        inv.setItem(14, new ItemBuilder(Material.STONE_BRICKS).setDisplayName("&dBeta").toItemStack());

        inv.setItem(22, new ItemBuilder(Material.BARRIER).setDisplayName("Retour").toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case AMETHYST_BLOCK -> PVPGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Alpha").addPlayer(player);
            case STONE_BRICKS -> PVPGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Beta").addPlayer(player);

            case BARRIER -> GameAPI.getInstance().getInventoryManager().openInventory(new MinigamesInventory(), player);

            default -> { }
        }
    }
}