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

public class SantaInventory extends AbstractInventory {

    private final Player player;
    private final WinterPlayerData winterPlayerData;

    public SantaInventory(Player player) {
        super(Component.text("Atelier du Père Noël", NamedTextColor.RED), 27);
        this.player = player;
        this.winterPlayerData = GameAPI.getInstance().getPlayerManager().getPlayerData(player).getWinterPlayerData();
    }

    @Override
    protected void addItems(Inventory inventory) {
        setItem(4, new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(Bukkit.createProfile(player.getName()))
                .displayName(Component.text("Ton profil", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                .lore(Component.text("Points d'événement : ", NamedTextColor.WHITE)
                                .append(Component.text(winterPlayerData.getWinterPoints(), NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, false),
                        Component.text("Cadeaux ramassés : ", NamedTextColor.WHITE)
                                .append(Component.text(winterPlayerData.getGifts(), NamedTextColor.YELLOW)).decoration(TextDecoration.ITALIC, false))
                .toItemStack());

        setItem(11, new ItemBuilder(Material.CHEST)
                .itemName(Component.text("Récompenses de Noël", NamedTextColor.GREEN))
                .lore(Component.text("Obtiens tes cadeaux avec tes points de l'événement !", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .toItemStack());

        setItem(13, new ItemBuilder(Material.SNOWBALL)
                .itemName(Component.text("Infos sur l'événement", NamedTextColor.AQUA))
                .lore(Component.text("Découvre les règles, et comment gagner des points !", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .toItemStack());

        setItem(15, new ItemBuilder(Material.EMERALD)
                .itemName(Component.text("Classement des joueurs", NamedTextColor.GOLD))
                .lore(Component.text("Consulte ton rang et ton nombre de points.", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .toItemStack());
    }


    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        switch (item.getType()) {
            case PLAYER_HEAD -> {
                player.sendMessage(Component.newline());
                player.sendMessage(Component.text("Ton profil Noël 2025", NamedTextColor.GOLD));

                player.sendMessage(Component.text("Points d'événement : ", NamedTextColor.WHITE)
                        .append(Component.text(winterPlayerData.getWinterPoints(), NamedTextColor.YELLOW)));

                player.sendMessage(Component.text("Cadeaux ramassés : ", NamedTextColor.WHITE)
                        .append(Component.text(winterPlayerData.getGifts(), NamedTextColor.YELLOW)));

                player.sendMessage(Component.text("- Continue à jouer pour débloquer des récompenses !", NamedTextColor.YELLOW));
            }

            case CHEST -> {
                player.sendMessage(Component.text("Tu ouvres le menu des récompenses ! - TODO", NamedTextColor.GREEN));
                // TODO: réfléchir aux récompenses (cosmétiques ?) - ouvrir un autre inventaire (SantaRewardsInventory)
            }

            case SNOWBALL -> {
                player.sendMessage(Component.newline());
                player.sendMessage("§6Informations sur l'événement Noël 2025");
                player.sendMessage("§e- Chaque partie de Cache-Cache te permet de trouver des cadeaux !");
                player.sendMessage("§e- Chaque cadeau te rapporte §a1 à 3 points d'événement §ealéatoirement.");
                player.sendMessage("§e- Tu ne peux ramasser qu'un cadeau par partie.");
                player.sendMessage("§e- Les points s'accumulent et peuvent être échangés contre des récompenses via le PNJ Père Noël.");
                player.sendMessage("§e- Objectif global : si la communauté trouve suffisamment de cadeaux, une récompense sera donnée à tous les joueurs !");
            }

            case EMERALD -> GameAPI.getInstance().getInventoryManager().openInventory(new SantaLeaderboardInventory(), player);

            default -> {}
        }
    }

}