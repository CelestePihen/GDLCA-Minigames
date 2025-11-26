package fr.cel.gameapi.inventory;

import com.destroystokyo.paper.profile.PlayerProfile;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FriendsInventory extends AbstractInventory {

    private final Player player;

    public FriendsInventory(Player player) {
        super(Component.text("Amis"), 54);
        this.player = player;
    }

    @Override
    protected void addItems(@NotNull Inventory inv) {
        for (String friendUUIDStr : GameAPI.getInstance().getFriendsManager().getFriendsUUIDList(player)) {
            UUID friendUUID = UUID.fromString(friendUUIDStr);

            OfflinePlayer friend = Bukkit.getOfflinePlayer(friendUUID);
            PlayerProfile playerProfile = friend.getPlayerProfile();

            Component statusColor = friend.isOnline() ? Component.text("En ligne", NamedTextColor.GREEN) : Component.text("Hors-ligne", NamedTextColor.RED);

            Component friendName = playerProfile.getName() != null ? Component.text(playerProfile.getName(), NamedTextColor.WHITE) : Component.text("Ce joueur doit se (re)connecter pour avoir son pseudo");

            ItemStack skull = new ItemBuilder(Material.PLAYER_HEAD)
                    .addLoreLine(statusColor)
                    .setSkullOwner(playerProfile)
                    .customName(friendName.decoration(TextDecoration.ITALIC, false))
                    .hideComponents("minecraft:profile")
                    .toItemStack();

            inv.addItem(skull);
        }

        setItem(51, new ItemBuilder(Material.BARRIER).itemName(Component.text("Quitter", NamedTextColor.RED)).toItemStack());
    }

    @Override
    public void interact(@NotNull Player player, @NotNull String itemName, @NotNull ItemStack item) {
        // TODO afficher le "profil" de l'ami ?
        if (item.getType() == Material.PLAYER_HEAD) {
            player.sendMessage(GameAPI.getPrefix().append(Component.text("Bientôt...")));
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