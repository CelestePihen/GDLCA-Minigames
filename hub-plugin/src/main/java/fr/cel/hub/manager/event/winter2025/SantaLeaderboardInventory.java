package fr.cel.hub.manager.event.winter2025;

import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.manager.database.event.WinterPlayerData;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SantaLeaderboardInventory extends AbstractInventory {

    public SantaLeaderboardInventory() {
        super(Component.text("Classement Noël 2025", NamedTextColor.GOLD), 27);
    }

    @Override
    protected void addItems(Inventory inv) {
        List<UUID> sortedPlayers = getTopPlayers(9);
        int slot = 9;

        for (int i = 0; i < sortedPlayers.size(); i++) {
            UUID uuid = sortedPlayers.get(i);
            WinterPlayerData data = new WinterPlayerData(uuid);

            String playerName = Bukkit.getOfflinePlayer(uuid).getName();
            if (playerName == null) playerName = "Inconnu";

            int points = data.getWinterPoints();

            ItemStack item = new ItemBuilder(Material.PLAYER_HEAD)
                    .setSkullOwner(Bukkit.createProfile(uuid))
                    .displayName(Component.text("#" + (i + 1) + " " + playerName, NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                    .lore(Component.text("Points : " + points, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                    .toItemStack();

            setItem(slot, item);
            slot++;
        }
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {

    }

    /**
     * Récupère les UUID des meilleurs joueurs triés par points
     */
    private List<UUID> getTopPlayers(int limit) {
        return WinterPlayerData.getAllPlayersSortedByPoints().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

}