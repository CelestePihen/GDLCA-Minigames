package fr.cel.gameapi.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class FriendsInventory extends AbstractInventory {

    private final Player player;

    public FriendsInventory(Player player) {
        super("Amis", 54);
        this.player = player;
    }

    @Override
    protected void addItems(Inventory inv) {
        for (String friendUUID : GameAPI.getInstance().getFriendsManager().getFriendsUUIDList(player)) {
            inv.addItem(new ItemBuilder(Material.PLAYER_HEAD)
                    .setDisplayName(Bukkit.getOfflinePlayer(UUID.fromString(friendUUID)).getName())
                    .setSkullOwner(Bukkit.createPlayerProfile(UUID.fromString(friendUUID)))
                    .toItemStack()
            );
        }
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {

    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }
}