package fr.cel.hub.inventory.cachecache;

import fr.cel.cachecache.manager.CCMapManager;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CCV2Inventory extends AbstractInventory {

    private final String nameV1;
    private final String nameV2;
    private final Material type;

    public CCV2Inventory(String mapName, Material type) {
        super(Component.text("Cache-Cache - " + mapName), 27);
        this.nameV1 = mapName + " V1";
        this.nameV2 = mapName + " V2";
        this.type = type;
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(12, new ItemBuilder(this.type).itemName(Component.text(nameV1, NamedTextColor.LIGHT_PURPLE)).toItemStack());
        inv.setItem(14, new ItemBuilder(this.type, 2).itemName(Component.text(nameV2, NamedTextColor.LIGHT_PURPLE)).toItemStack());

        inv.setItem(22, new ItemBuilder(Material.BARRIER).itemName(Component.text("Retour")).toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (item.getType() == Material.BARRIER) {
            GameAPI.getInstance().getInventoryManager().openInventory(new CacheCacheInventory(), player);
            return;
        }

        if (itemName.equals(nameV1)) {
            CCMapManager.getMapManager().getMapByDisplayName(nameV1).addPlayer(player, false);
        } else if (itemName.equals(nameV2)) {
            CCMapManager.getMapManager().getMapByDisplayName(nameV2).addPlayer(player, false);
        }
    }

}
