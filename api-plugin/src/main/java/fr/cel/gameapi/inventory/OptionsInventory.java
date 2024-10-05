package fr.cel.gameapi.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.PlayerData;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OptionsInventory extends AbstractInventory {

    private final PlayerData playerData;

    private final ItemStack greenDye = new ItemBuilder(Material.GREEN_DYE).setDisplayName("Demandes d'amis").addLoreLine(ChatUtility.format("Vous acceptez les demandes d'amis.", ChatUtility.GREEN)).toItemStack();
    private final ItemStack redDye = new ItemBuilder(Material.RED_DYE).setDisplayName("Demandes d'amis").addLoreLine(ChatUtility.format("Vous n'acceptez pas les demandes d'amis.", ChatUtility.DARK_RED)).toItemStack();

    public OptionsInventory(Player player) {
        super("Options", 9);
        this.playerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player);
    }

    // Si le colorant est vert, cela veut dire que le joueur accepte les demandes d'amis. En rouge, si c'est le contraire
    @Override
    protected void addItems(Inventory inv) {
        if (playerData.isAllowingFriends()) {
            inv.addItem(greenDye);
        } else {
            inv.addItem(redDye);
        }
    }

    // Si le colorant est vert, cela veut dire que le joueur accepte les demandes d'amis. En rouge, si c'est le contraire
    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (item.getType() == Material.GREEN_DYE) {
            playerData.setAllowFriends(false);
            setItem(0, greenDye);
            player.sendMessage(GameAPI.getPrefix() + "Vous avez désactivé les demandes d'amis.");
        }

        else if (item.getType() == Material.RED_DYE) {
            playerData.setAllowFriends(true);
            setItem(0, redDye);
            player.sendMessage(GameAPI.getPrefix() + "Vous avez activé les demandes d'amis.");
        }
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}