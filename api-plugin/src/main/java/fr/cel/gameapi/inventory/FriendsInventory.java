package fr.cel.gameapi.inventory;

import com.destroystokyo.paper.profile.PlayerProfile;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ChatUtility;
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
        for (String friendUUIDStr : GameAPI.getInstance().getFriendsManager().getFriendsUUIDList(player)) {
            UUID friendUUID = UUID.fromString(friendUUIDStr);

            OfflinePlayer friend = Bukkit.getOfflinePlayer(friendUUID);
            String friendName = friend.getName() != null ? friend.getName() : "Ce joueur doit se (re)connecter pour avoir son pseudo";

            boolean isOnline = friend.isOnline();
            String statusColor = isOnline ? "&aEn ligne" : "&cHors-ligne";

            PlayerProfile playerProfile = Bukkit.createProfile(friendUUID);

            ItemStack skull = new ItemBuilder(Material.PLAYER_HEAD)
                    .setLore(ChatUtility.format(statusColor))
                    .setSkullOwner(playerProfile)
                    .setDisplayName("&f" + friendName)
                    .toItemStack();

            inv.addItem(skull);
        }
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        // TODO afficher le "profil" de l'ami ?
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}