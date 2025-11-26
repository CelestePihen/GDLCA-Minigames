package fr.cel.gameapi.inventory;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.database.PlayerData;
import fr.cel.gameapi.manager.database.event.WinterPlayerData;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ProfileInventory extends AbstractInventory {

    private final Player player;
    private final PlayerData playerData;
    private final WinterPlayerData winterPlayerData;

    public ProfileInventory(Player player) {
        super(Component.text("Mon Profil"), 27);
        this.player = player;
        this.playerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player);
        this.winterPlayerData = this.playerData.getWinterPlayerData();
    }

    @Override
    protected void addItems(@NotNull Inventory inv) {
        double coins = playerData.getCoins();
        Component coinsStr = Component.text("");
        if (coins <= 1) {
            coinsStr = coinsStr.append(Component.text(coins + " pièce"));
        } else {
            coinsStr = coinsStr.append(Component.text(coins + " pièces"));
        }

        inv.setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(player.getPlayerProfile())
                .customName(Component.text(player.getName()).decoration(TextDecoration.ITALIC, false))
                .lore(coinsStr.color(NamedTextColor.GOLD),
                        Component.text("Flocons de Noël : ", NamedTextColor.AQUA)
                                .append(Component.text(winterPlayerData.getWinterPoints(), NamedTextColor.YELLOW)),
                        Component.text("Cadeaux ramassés : ", NamedTextColor.AQUA)
                                .append(Component.text(winterPlayerData.getGifts(), NamedTextColor.YELLOW)))
                .toItemStack());

        inv.setItem(10, new ItemBuilder(Material.CANDLE)
                .itemName(Component.text("Amis", NamedTextColor.GRAY))
                .toItemStack());

        inv.setItem(12, new ItemBuilder(Material.ENDER_PEARL)
                .itemName(Component.text("Partie", NamedTextColor.AQUA))
                .addLoreLine(Component.text("(À venir)", NamedTextColor.DARK_GRAY))
                .toItemStack());

        inv.setItem(14, new ItemBuilder(Material.PAPER)
                .itemName(Component.text("Statistiques", NamedTextColor.GREEN))
                .toItemStack());

        inv.setItem(16, new ItemBuilder(Material.CLOCK)
                .itemName(Component.text("Options", NamedTextColor.BLUE))
                .toItemStack());
    }

    @Override
    public void interact(@NotNull Player player, @NotNull String itemName, @NotNull ItemStack item) {
        switch (item.getType()) {
            case PLAYER_HEAD -> player.sendMessage(GameAPI.getPrefix().append(Component.text("C'est moi.", NamedTextColor.RED)));
            case CANDLE -> GameAPI.getInstance().getInventoryManager().openInventory(new FriendsInventory(player), player);
            case PAPER -> GameAPI.getInstance().getInventoryManager().openInventory(new StatisticsInventory(player), player);
            case CLOCK -> GameAPI.getInstance().getInventoryManager().openInventory(new OptionsInventory(player), player);
            default -> {}
        }
    }

}