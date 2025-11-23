package fr.cel.hub.manager.event.winter2025;

import fr.cel.gameapi.GameAPI;
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
import org.jetbrains.annotations.NotNull;

public class SantaInventory extends AbstractInventory {

    private final Player player;
    private final WinterPlayerData winterPlayerData;

    public SantaInventory(Player player) {
        super(Component.text("Atelier du Père Noël", NamedTextColor.RED), 27);
        this.player = player;
        this.winterPlayerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player).getWinterPlayerData();
    }

    @Override
    protected boolean makeGlassPane() {
        return false;
    }

    @Override
    protected void addItems(@NotNull Inventory inventory) {
        for (int i = 0; i < getSize(); i++) {
            Material decoration = (i % 2 == 0) ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE;
            setItem(i, new ItemBuilder(decoration).hideTooltip().toItemStack());
        }

        setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(Bukkit.createProfile(player.getName()))
                .customName(Component.text("Ton profil", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                .lore(Component.text("Flocons de Noël : ", NamedTextColor.AQUA)
                                .append(Component.text(winterPlayerData.getWinterPoints(), NamedTextColor.YELLOW)),
                        Component.text("Cadeaux ramassés : ", NamedTextColor.AQUA)
                                .append(Component.text(winterPlayerData.getGifts(), NamedTextColor.YELLOW)))
                .toItemStack());

        setItem(11, new ItemBuilder(Material.CHEST)
                .itemName(Component.text("Récompenses de Noël", NamedTextColor.GREEN))
                .addLoreLine(Component.text("Obtiens tes cadeaux avec tes flocons de Noël !", NamedTextColor.YELLOW))
                .toItemStack());

        setItem(13, new ItemBuilder(Material.SNOWBALL)
                .itemName(Component.text("Informations sur l'événement Noël 2025", NamedTextColor.AQUA))
                .lore(
                        Component.text("- Chaque ", NamedTextColor.YELLOW).append(Component.text("partie de Cache-Cache", NamedTextColor.GREEN)).append(Component.text(" te", NamedTextColor.YELLOW)),
                        Component.text("  permet de trouver des ", NamedTextColor.YELLOW).append(Component.text("cadeaux", NamedTextColor.GREEN)).append(Component.text(" !", NamedTextColor.YELLOW)),
                        Component.empty(),
                        Component.text("- Chaque cadeau te rapporte ", NamedTextColor.YELLOW)
                                .append(Component.text("2 à 5", NamedTextColor.GREEN)),
                        Component.text("  ", NamedTextColor.YELLOW)
                                .append(Component.text("flocons de Noël", NamedTextColor.GREEN))
                                .append(Component.text(" aléatoirement.", NamedTextColor.YELLOW)),
                        Component.empty(),
                        Component.text("- Tu ne peux ramasser ", NamedTextColor.YELLOW).append(Component.text("qu'un cadeau", NamedTextColor.GREEN)),
                        Component.text("  par partie", NamedTextColor.GREEN).append(Component.text(".", NamedTextColor.YELLOW)),
                        Component.empty(),
                        Component.text("- Les flocons de Noël s'accumulent et peuvent", NamedTextColor.YELLOW),
                        Component.text("  être échangés contre des récompenses", NamedTextColor.GREEN),
                        Component.text("  via le PNJ Père Noël.", NamedTextColor.YELLOW),
                        Component.empty(),
                        Component.text("- Objectif global : si la communauté", NamedTextColor.YELLOW),
                        Component.text("  trouve suffisamment de cadeaux,", NamedTextColor.YELLOW),
                        Component.text("  une récompense sera donnée", NamedTextColor.GREEN),
                        Component.text("  à tous les joueurs !", NamedTextColor.GREEN))
                .toItemStack());

        setItem(15, new ItemBuilder(Material.EMERALD)
                .itemName(Component.text("Classement des joueurs", NamedTextColor.GOLD))
                .addLoreLine(Component.text("Consulte ton rang et ton nombre de points.", NamedTextColor.YELLOW))
                .toItemStack());
    }

    @Override
    public void interact(@NotNull Player player, @NotNull String itemName, @NotNull ItemStack item) {
        switch (item.getType()) {
            case CHEST -> new ChristmasShopGUI(player).open(player);

            case EMERALD -> GameAPI.getInstance().getInventoryManager().openInventory(new SantaLeaderboardInventory(), player);

            default -> {}
        }
    }

}