package fr.cel.hub.inventory.cachecache;

import fr.cel.cachecache.manager.CCGameManager;
import fr.cel.hub.Hub;
import fr.cel.hub.inventory.AbstractInventory;
import fr.cel.hub.utils.ChatUtility;
import fr.cel.hub.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CCV2Inventory extends AbstractInventory {

    private final String nameV1;
    private final String nameV2;
    @Getter private final Material type;

    public CCV2Inventory(String mapName, Material type, Hub main) {
        super("Cache-Cache - " + mapName, 27, main);
        this.nameV1 = mapName + " V1";
        this.nameV2 = mapName + " V2";
        this.type = type;
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(12, new ItemBuilder(this.type).setDisplayName(ChatUtility.format("&d" + nameV1)).toItemStack());
        inv.setItem(14, new ItemBuilder(this.type, 2).setDisplayName(ChatUtility.format("&d" + nameV2)).toItemStack());

        inv.setItem(22, new ItemBuilder(Material.BARRIER).setDisplayName("Retour").toItemStack());
    }

    @Override
    protected void interact(Player player, String itemName, ItemStack item, Hub main) {

        if (item.getType() == Material.BARRIER) {
            player.openInventory(inventoryManager.getInventories().get("cachecache").getInv());
            return;
        }

        if (ChatUtility.stripColor(itemName).equals(nameV1)) {
            CCGameManager.getGameManager().getArenaManager().getArenaByDisplayName(nameV1).addPlayer(player, false);
        } else if (ChatUtility.stripColor(itemName).equals(nameV2)) {
            CCGameManager.getGameManager().getArenaManager().getArenaByDisplayName(nameV2).addPlayer(player, false);
        }
    }

}
