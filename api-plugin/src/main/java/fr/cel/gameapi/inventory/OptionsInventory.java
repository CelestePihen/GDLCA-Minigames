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
import org.jetbrains.annotations.NotNull;

public class OptionsInventory extends AbstractInventory {

    private final PlayerData playerData;

    private static final ItemStack ALLOW_FRIENDS_GREEN = new ItemBuilder(Material.GREEN_DYE).itemName(Component.text("Demandes d'amis")).addLoreLine(Component.text("Tu acceptes les demandes d'amis.", NamedTextColor.GREEN)).toItemStack();
    private static final ItemStack ALLOW_FRIENDS_RED = new ItemBuilder(Material.RED_DYE).itemName(Component.text("Demandes d'amis")).addLoreLine(Component.text("Tu n'acceptes pas les demandes d'amis.", NamedTextColor.DARK_RED)).toItemStack();

    public OptionsInventory(Player player) {
        super(Component.text("Options"), 9);
        this.playerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player);
    }

    // Si le colorant est vert, cela veut dire que le joueur accepte les demandes d'amis. En rouge, si c'est le contraire
    @Override
    protected void addItems(@NotNull Inventory inv) {
        if (playerData.isAllowingFriends()) {
            inv.addItem(ALLOW_FRIENDS_GREEN);
        } else {
            inv.addItem(ALLOW_FRIENDS_RED);
        }

        setItem(8, new ItemBuilder(Material.BARRIER).itemName(Component.text("Fermer", NamedTextColor.RED)).toItemStack());
    }

    // Si le colorant est vert, cela veut dire que le joueur accepte les demandes d'amis. En rouge, si c'est le contraire
    @Override
    public void interact(@NotNull Player player, @NotNull String itemName, @NotNull ItemStack item) {
        if (item.getType() == Material.GREEN_DYE) {
            playerData.setAllowFriends(false);
            setItem(0, ALLOW_FRIENDS_RED);
            player.sendMessage(GameAPI.getPrefix().append(Component.text("Tu as désactivé les demandes d'amis.")));
        }

        else if (item.getType() == Material.RED_DYE) {
            playerData.setAllowFriends(true);
            setItem(0, ALLOW_FRIENDS_GREEN);
            player.sendMessage(GameAPI.getPrefix().append(Component.text("Tu as activé les demandes d'amis.")));
        }

        else if (item.getType() == Material.BARRIER) {
            new ProfileInventory(player).open(player);
        }
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}