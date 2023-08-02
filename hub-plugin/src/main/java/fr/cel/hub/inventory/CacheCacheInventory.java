package fr.cel.hub.inventory;

import fr.cel.cachecache.manager.CCGameManager;
import fr.cel.hub.Hub;
import fr.cel.hub.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CacheCacheInventory extends AbstractInventory {

    public CacheCacheInventory(Hub main) {
        super("Cache-Cache", 54, main);
    }

    @Override
    protected void addItems(Inventory inv) {
        inv.setItem(1, new ItemBuilder(Material.SCULK_CATALYST).setDisplayName(Component.text("Warden", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(3, new ItemBuilder(Material.MINECART).setDisplayName(Component.text("Mine", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(5, new ItemBuilder(Material.STONE_BRICKS).setDisplayName(Component.text("Bunker", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(7, new ItemBuilder(Material.WHEAT).setDisplayName(Component.text("Moulin", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());

        inv.setItem(19, new ItemBuilder(Material.BRICKS).setDisplayName(Component.text("Ville", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(21, new ItemBuilder(Material.WAXED_COPPER_BLOCK).setDisplayName(Component.text("Steampunk", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(23, new ItemBuilder(Material.SAND).setDisplayName(Component.text("Désert", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());
        inv.setItem(25, new ItemBuilder(Material.SLIME_BALL).setDisplayName(Component.text("Marais", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)).toItemStack());

        for (int slot = 37; slot <= 43; slot += 2) {
            inv.setItem(slot, new ItemBuilder(Material.COMMAND_BLOCK).setDisplayName(Component.text("Indisponible", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)).toItemStack());
        }

        inv.setItem(49, new ItemBuilder(Material.BARRIER).setDisplayName(Component.text("Retour").decoration(TextDecoration.ITALIC, false)).toItemStack());
    }

    @Override
    protected void interact(Player player, Component itemName, Material type, Hub main) {
        switch (type) {
            case SCULK_CATALYST -> CCGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Warden").addPlayer(player);
            case MINECART -> CCGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Mine").addPlayer(player);
            case STONE_BRICKS -> CCGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Bunker").addPlayer(player);
            case WHEAT -> player.openInventory(inventoryManager.getInventories().get("ccMoulin").getInv());
            case BRICKS -> CCGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Ville").addPlayer(player);
            case WAXED_COPPER_BLOCK -> player.openInventory(inventoryManager.getInventories().get("ccSteampunk").getInv());
            case SAND -> CCGameManager.getGameManager().getArenaManager().getArenaByDisplayName("Désert").addPlayer(player);
            case SLIME_BALL -> player.openInventory(inventoryManager.getInventories().get("ccMarais").getInv());

            case COMMAND_BLOCK -> {
                sendMessageWithPrefix(player, "Indisponible pour le moment.");
                player.closeInventory();
            }

            case BARRIER -> player.openInventory(inventoryManager.getInventories().get("minigames").getInv());

            default -> { }
        }
    }

}