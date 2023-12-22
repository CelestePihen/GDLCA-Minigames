package fr.cel.hub.inventory.cachecache;

import fr.cel.cachecache.manager.CCGameManager;
import fr.cel.hub.Hub;
import fr.cel.hub.inventory.AbstractInventory;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.hub.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CacheCacheInventory extends AbstractInventory {

    public CacheCacheInventory(Hub main) {
        super("Cache-Cache", 54, main);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(1, new ItemBuilder(Material.SCULK_CATALYST).setDisplayName(ChatUtility.format("&dWarden")).toItemStack());
        inv.setItem(3, new ItemBuilder(Material.MINECART).setDisplayName(ChatUtility.format("&dMine")).toItemStack());
        inv.setItem(5, new ItemBuilder(Material.STONE_BRICKS).setDisplayName(ChatUtility.format("&dBunker")).toItemStack());
        inv.setItem(7, new ItemBuilder(Material.WHEAT).setDisplayName(ChatUtility.format("&dMoulin")).toItemStack());

        inv.setItem(19, new ItemBuilder(Material.BRICKS).setDisplayName(ChatUtility.format("&dVille")).toItemStack());
        inv.setItem(21, new ItemBuilder(Material.WAXED_COPPER_BLOCK).setDisplayName(ChatUtility.format("&dSteampunk")).toItemStack());
        inv.setItem(23, new ItemBuilder(Material.SAND).setDisplayName(ChatUtility.format("&dDésert")).toItemStack());
        inv.setItem(25, new ItemBuilder(Material.SLIME_BALL).setDisplayName(ChatUtility.format("&dMarais")).toItemStack());

        for (int slot = 37; slot <= 43; slot += 2) {
            inv.setItem(slot, new ItemBuilder(Material.COMMAND_BLOCK).setDisplayName(ChatUtility.format("&eMaisons")).toItemStack());
        }

        inv.setItem(49, new ItemBuilder(Material.BARRIER).setDisplayName("Retour").toItemStack());
    }

    @Override
    protected void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case SCULK_CATALYST -> CCGameManager.getGameManager().getArenaManager().getArenas().get("warden").addPlayer(player, false);
            case MINECART -> CCGameManager.getGameManager().getArenaManager().getArenas().get("mine").addPlayer(player, false);
            case STONE_BRICKS -> CCGameManager.getGameManager().getArenaManager().getArenas().get("bunker").addPlayer(player, false);
            case WHEAT -> player.openInventory(inventoryManager.getInventories().get("ccMoulin").getInv());
            case BRICKS -> CCGameManager.getGameManager().getArenaManager().getArenas().get("ville").addPlayer(player, false);
            case WAXED_COPPER_BLOCK -> player.openInventory(inventoryManager.getInventories().get("ccSteampunk").getInv());
            case SAND -> CCGameManager.getGameManager().getArenaManager().getArenas().get("desert").addPlayer(player, false);
            case SLIME_BALL -> player.openInventory(inventoryManager.getInventories().get("ccMarais").getInv());

            case COMMAND_BLOCK -> CCGameManager.getGameManager().getArenaManager().getTemporaryHub().addPlayer(player);

            case BARRIER -> player.openInventory(inventoryManager.getInventories().get("minigames").getInv());

            default -> { }
        }
    }

}