package fr.cel.hub.manager.event.winter2025;

import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.manager.database.event.WinterPlayerData;
import fr.cel.gameapi.manager.inventory.InventoryTypes;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SantaLeaderboardInventory extends AbstractInventory {

    public SantaLeaderboardInventory() {
        super(Component.text("Classement Noël 2025", NamedTextColor.GOLD), 27);
        this.type = InventoryTypes.GLOBAL;
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

    @Override
    protected void addItems(@NotNull Inventory inv) {
        for (int i = 0; i < 9; i++) {
            Material decoration = (i % 2 == 0) ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE;
            setItem(i, new ItemBuilder(decoration).hideTooltip().toItemStack());
        }

        for (int i = 18; i < getSize(); i++) {
            Material decoration = (i % 2 == 0) ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE;
            setItem(i, new ItemBuilder(decoration).hideTooltip().toItemStack());
        }

        List<UUID> sortedPlayers = getTopPlayers();
        int slot = 9;

        for (int i = 0; i < sortedPlayers.size(); i++) {
            UUID uuid = sortedPlayers.get(i);
            WinterPlayerData data = new WinterPlayerData(uuid);

            String playerName = Bukkit.getOfflinePlayer(uuid).getName();
            if (playerName == null) playerName = "Inconnu";

            int points = data.getWinterPoints();

            ItemStack item = new ItemBuilder(Material.PLAYER_HEAD)
                    .setSkullOwner(Bukkit.createProfile(uuid))
                    .customName(Component.text("#" + (i + 1) + " " + playerName, NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                    .lore(Component.text("Points : ", NamedTextColor.AQUA).append(Component.text(points, NamedTextColor.YELLOW)))
                    .hideComponents("minecraft:profile")
                    .toItemStack();

            setItem(slot, item);
            slot++;
        }

        setItem(22, new ItemBuilder(Material.BARRIER).itemName(Component.text("Quitter")).toItemStack());
    }

    @Override
    public void interact(@NotNull Player player, @NotNull String itemName, @NotNull ItemStack item) {
        if (item.getType() == Material.BARRIER) new SantaInventory(player).open(player);
    }

    /**
     * Récupère les UUID des meilleurs joueurs triés par points
     */
    private List<UUID> getTopPlayers() {
        return WinterPlayerData.getAllPlayersSortedByPoints().stream()
                .limit(9)
                .collect(Collectors.toList());
    }

}