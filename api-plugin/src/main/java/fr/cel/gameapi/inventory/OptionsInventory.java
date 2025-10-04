package fr.cel.gameapi.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.PlayerData;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OptionsInventory extends AbstractInventory {

    private final PlayerData playerData;

    private static final ItemStack ALLOW_FRIENDS_GREEN = new ItemBuilder(Material.GREEN_DYE).itemName(Component.text("Demandes d'amis")).addLoreLine(Component.text("Vous acceptez les demandes d'amis.", NamedTextColor.GREEN)).toItemStack();
    private static final ItemStack ALLOW_FRIENDS_RED = new ItemBuilder(Material.RED_DYE).itemName(Component.text("Demandes d'amis")).addLoreLine(Component.text("Vous n'acceptez pas les demandes d'amis.", NamedTextColor.DARK_RED)).toItemStack();

    public OptionsInventory(Player player) {
        super(Component.text("Options"), 9);
        this.playerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player);
    }

    // Si le colorant est vert, cela veut dire que le joueur accepte les demandes d'amis. En rouge, si c'est le contraire
    @Override
    protected void addItems(Inventory inv) {
        if (playerData.isAllowingFriends()) {
            inv.addItem(ALLOW_FRIENDS_GREEN);
        } else {
            inv.addItem(ALLOW_FRIENDS_RED);
        }
    }

    // Si le colorant est vert, cela veut dire que le joueur accepte les demandes d'amis. En rouge, si c'est le contraire
    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (item.getType() == Material.GREEN_DYE) {
            playerData.setAllowFriends(false);
            setItem(0, ALLOW_FRIENDS_RED);
            player.sendMessage(GameAPI.getPrefix().append(Component.text("Vous avez désactivé les demandes d'amis.")));
        }

        else if (item.getType() == Material.RED_DYE) {
            playerData.setAllowFriends(true);
            setItem(0, ALLOW_FRIENDS_GREEN);
            player.sendMessage(GameAPI.getPrefix().append(Component.text("Vous avez activé les demandes d'amis.")));
        }
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}