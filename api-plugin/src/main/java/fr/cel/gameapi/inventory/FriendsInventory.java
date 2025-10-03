package fr.cel.gameapi.inventory;

import com.destroystokyo.paper.profile.PlayerProfile;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        super(Component.text("Amis"), 54);
        this.player = player;
    }

    @Override
    protected void addItems(Inventory inv) {
        for (String friendUUIDStr : GameAPI.getInstance().getFriendsManager().getFriendsUUIDList(player)) {
            UUID friendUUID = UUID.fromString(friendUUIDStr);

            OfflinePlayer friend = Bukkit.getOfflinePlayer(friendUUID);
            Component friendName = friend.getName() != null ? Component.text(friend.getName(), NamedTextColor.WHITE) : Component.text("Ce joueur doit se (re)connecter pour avoir son pseudo");

            boolean isOnline = friend.isOnline();
            Component statusColor = isOnline ? Component.text("En ligne", NamedTextColor.GREEN) : Component.text("Hors-ligne", NamedTextColor.RED);

            PlayerProfile playerProfile = Bukkit.createProfile(friendUUID);

            ItemStack skull = new ItemBuilder(Material.PLAYER_HEAD)
                    .addLoreLine(statusColor)
                    .setSkullOwner(playerProfile)
                    .displayName(friendName)
                    .toItemStack();

            inv.addItem(skull);
        }
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        // TODO afficher le "profil" de l'ami ?
        player.sendMessage(GameAPI.getPrefix().append(Component.text("Bientôt...")));
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

}