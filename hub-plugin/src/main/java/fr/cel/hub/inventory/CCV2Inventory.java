package fr.cel.hub.inventory;

import fr.cel.cachecache.manager.CCGameManager;
import fr.cel.hub.Hub;
import fr.cel.hub.inventory.AbstractInventory;
import fr.cel.hub.utils.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CCV2Inventory extends AbstractInventory {

    private final String nameV1;
    private final String nameV2;
    @Getter private Material type;

    public CCV2Inventory(String mapName, Material type, Hub main) {
        super("Cache-Cache - " + mapName, 27, main);
        this.nameV1 = mapName + " V1";
        this.nameV2 = mapName + " V2";
        this.type = type;
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(12, new ItemBuilder(this.type).setDisplayName(Component.text(nameV1, NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(14, new ItemBuilder(this.type, 2).setDisplayName(Component.text(nameV2, NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());

        inv.setItem(22, new ItemBuilder(Material.BARRIER).setDisplayName(Component.text("Retour").decoration(TextDecoration.ITALIC, false)).toItemStack());
    }

    @Override
    protected void interact(Player player, Component itemName, Material type, Hub main) {

        if (type == Material.BARRIER) {
            player.openInventory(inventoryManager.getInventories().get("cachecache").getInv());
            return;
        }

        Component name = itemName.color(NamedTextColor.WHITE);
        Component v1 = Component.text(nameV1, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        Component v2 = Component.text(nameV2, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);

        if (name.equals(v1)) {
            player.sendMessage(nameV1 + " | " + nameV2);
            CCGameManager.getGameManager().getArenaManager().getArenaByDisplayName(nameV1).addPlayer(player);
        } else if (name.equals(v2)) {
            CCGameManager.getGameManager().getArenaManager().getArenaByDisplayName(nameV2).addPlayer(player);
        }
    }

}
