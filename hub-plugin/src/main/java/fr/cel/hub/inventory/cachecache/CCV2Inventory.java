package fr.cel.hub.inventory.cachecache;

import fr.cel.cachecache.manager.CCArenaManager;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CCV2Inventory extends AbstractInventory {

    private final String nameV1;
    private final String nameV2;
    private final Material type;

    public CCV2Inventory(String mapName, Material type) {
        super("Cache-Cache - " + mapName, 27);
        this.nameV1 = mapName + " V1";
        this.nameV2 = mapName + " V2";
        this.type = type;
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(12, new ItemBuilder(this.type).setDisplayName("&d" + nameV1).toItemStack());
        inv.setItem(14, new ItemBuilder(this.type, 2).setDisplayName("&d" + nameV2).toItemStack());

        inv.setItem(22, new ItemBuilder(Material.BARRIER).setDisplayName("Retour").toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (item.getType() == Material.BARRIER) {
            GameAPI.getInstance().getInventoryManager().openInventory(new CacheCacheInventory(), player);
            return;
        }

        if (ChatUtility.stripColor(itemName).equals(nameV1)) {
            CCArenaManager.getArenaManager().getArenaByDisplayName(nameV1).addPlayer(player, false);
        } else if (ChatUtility.stripColor(itemName).equals(nameV2)) {
            CCArenaManager.getArenaManager().getArenaByDisplayName(nameV2).addPlayer(player, false);
        }
    }

}
