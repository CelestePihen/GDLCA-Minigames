package fr.cel.hub.manager.event.winter2025;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.manager.database.event.WinterPlayerData;
import fr.cel.gameapi.manager.inventory.InventoryTypes;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SantaInventory extends AbstractInventory {

    private static final PlayerProfile GIFT_PROFILE;

    private final Player player;
    private final WinterPlayerData winterPlayerData;

    static {
        GIFT_PROFILE = Bukkit.createProfile(UUID.randomUUID());
        GIFT_PROFILE.setProperty(new ProfileProperty("textures", HeadManager.VALUE_GIFT_TEXTURE));
    }

    public SantaInventory(Player player) {
        super(Component.text("Atelier du Père Noël", NamedTextColor.RED), 27);
        this.type = InventoryTypes.PERSONAL;
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

        setItem(13, new ItemBuilder(Material.PLAYER_HEAD)
                .customName(Component.text("Collection de têtes", NamedTextColor.GOLD))
                .addLoreLine(Component.text("Découvre toutes les têtes cachées !", NamedTextColor.YELLOW))
                .setSkullOwner(GIFT_PROFILE)
                .toItemStack());

        setItem(22, new ItemBuilder(Material.SNOWBALL)
                .itemName(Component.text("Informations sur l'événement Noël 2025", NamedTextColor.AQUA))
                .lore(
                        Component.text("- Pendant le ", NamedTextColor.YELLOW)
                                .append(Component.text("Cache-Cache", NamedTextColor.GREEN))
                                .append(Component.text(", tu peux trouver", NamedTextColor.YELLOW)),
                        Component.text("  des ", NamedTextColor.YELLOW)
                                .append(Component.text("cadeaux", NamedTextColor.GREEN))
                                .append(Component.text(" sur la carte !", NamedTextColor.YELLOW)),
                        Component.empty(),
                        Component.text("- Chaque cadeau rapporté au sapin donne ", NamedTextColor.YELLOW)
                                .append(Component.text("25 à 35", NamedTextColor.GREEN))
                                .append(Component.text(" étoiles.", NamedTextColor.YELLOW)),
                        Component.text("  (1 cadeau maximum par partie)", NamedTextColor.YELLOW),
                        Component.empty(),
                        Component.text("- En utilisant des objets, en survivant", NamedTextColor.YELLOW),
                        Component.text("  ou en éliminant des joueurs, tu", NamedTextColor.YELLOW),
                        Component.text("  gagnes aussi des ", NamedTextColor.GREEN)
                                .append(Component.text("flocons de neige", NamedTextColor.GREEN))
                                .append(Component.text(".", NamedTextColor.YELLOW)),
                        Component.empty(),
                        Component.text("- Dans le Hub, trouve des ", NamedTextColor.YELLOW)
                                .append(Component.text("têtes cachées", NamedTextColor.GREEN)),
                        Component.text("  pour obtenir encore plus de flocons de neige !", NamedTextColor.YELLOW),
                        Component.empty(),
                        Component.text("- Échange tes flocons de neige contre des", NamedTextColor.YELLOW),
                        Component.text("  récompenses exclusives auprès du", NamedTextColor.YELLOW),
                        Component.text("  PNJ ", NamedTextColor.YELLOW)
                                .append(Component.text("Père Noël", NamedTextColor.GREEN))
                                .append(Component.text(".", NamedTextColor.YELLOW)),
                        Component.empty(),
                        Component.text("- L'événement dure du ", NamedTextColor.YELLOW)
                                .append(Component.text("1er au 25 décembre", NamedTextColor.GREEN))
                                .append(Component.text(".", NamedTextColor.YELLOW))
                )
                .toItemStack());

        setItem(15, new ItemBuilder(Material.EMERALD)
                .itemName(Component.text("Classement des joueurs", NamedTextColor.GOLD))
                .addLoreLine(Component.text("Consulte ton rang et ton nombre de points.", NamedTextColor.YELLOW))
                .toItemStack());
    }

    @Override
    public void interact(@NotNull Player player, @NotNull String itemName, @NotNull ItemStack item) {
        if (item.getType() == Material.CHEST) {
            new ChristmasShopGUI(player).open(player);
            return;
        }

        if (item.getType() == Material.EMERALD) {
            GameAPI.getInstance().getInventoryManager().openInventory(new SantaLeaderboardInventory(), player);
            return;
        }

        if (item.getItemMeta() == null || item.getItemMeta().customName() == null) return;

        String customName = ((TextComponent)(item.getItemMeta().customName())).content();
        if (customName.equalsIgnoreCase("Collection de têtes")) {
            GameAPI.getInstance().getInventoryManager().openInventory(new HeadInventory(player, true), player);
            return;
        }
    }

}