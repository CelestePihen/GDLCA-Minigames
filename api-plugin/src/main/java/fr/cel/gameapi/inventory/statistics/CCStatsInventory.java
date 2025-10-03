package fr.cel.gameapi.inventory.statistics;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.inventory.StatisticsInventory;
import fr.cel.gameapi.manager.database.StatisticsManager;
import fr.cel.gameapi.manager.database.StatisticsManager.PlayerStatistics;
import fr.cel.gameapi.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CCStatsInventory extends AbstractInventory {

    private final Player player;

    /**
     * Constructeur de l'inventaire des statistiques Cache-Cache.
     * @param player Le joueur pour lequel les statistiques sont affichées.
     */
    public CCStatsInventory(Player player) {
        super(Component.text("Statistiques - Cache-Cache"), 27);
        this.player = player;
    }

    @Override
    protected void addItems(Inventory inv) {
        StatisticsManager statisticsManager = GameAPI.getInstance().getStatisticsManager();

        for (Statistic statistic : Statistic.values()) {
            inv.addItem(new ItemBuilder(statistic.material)
                    .itemName(statistic.name.color(NamedTextColor.YELLOW))
                    .lore(Component.text("Valeur : ").append(Component.text(statisticsManager.getPlayerStatistic(player, statistic.playerStatistics), NamedTextColor.GREEN)))
                    .toItemStack());
        }

        inv.setItem(22, new ItemBuilder(Material.BARRIER)
                .itemName(Component.text("Fermer", NamedTextColor.RED))
                .toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (item.getType() == Material.BARRIER) GameAPI.getInstance().getInventoryManager().openInventory(new StatisticsInventory(this.player), player);
    }

    private enum Statistic {
        GAMES_PLAYED(PlayerStatistics.CC_GAMES_PLAYED, Material.SPYGLASS, Component.text("Parties jouées")),
        SEEKER_COUNT(PlayerStatistics.CC_SEEKER_COUNT, Material.PLAYER_HEAD, Component.text("Nombre de fois chercheur")),
        HIDER_COUNT(PlayerStatistics.CC_HIDER_COUNT, Material.LEATHER_HELMET, Component.text("Nombre de fois caché")),
        ELIMINATIONS(PlayerStatistics.CC_ELIMINATIONS, Material.IRON_SWORD, Component.text("Éliminations")),
        BLINDNESS_USAGE(PlayerStatistics.CC_BLINDNESS_USAGE, Material.FERMENTED_SPIDER_EYE, Component.text("Utilisation d'Aveuglement")),
        INVISIBILITY_USAGE(PlayerStatistics.CC_INVISIBILITY_USAGE, Material.GOLDEN_CARROT, Component.text("Utilisation d'Invisibilité")),
        POINT_PLAYER_USAGE(PlayerStatistics.CC_POINT_PLAYER_USAGE, Material.FEATHER, Component.text("Utilisation de Pointer le joueur")),
        SOUND_USAGE(PlayerStatistics.CC_SOUND_USAGE, Material.NOTE_BLOCK, Component.text("Utilisation de Sons")),
        SPEED_USAGE(PlayerStatistics.CC_SPEED_USAGE, Material.SUGAR, Component.text("Utilisation de Vitesse")),
        ;

        private final PlayerStatistics playerStatistics;
        private final Material material;
        private final Component name;

        Statistic(StatisticsManager.PlayerStatistics playerStatistics, Material material, Component name) {
            this.playerStatistics = playerStatistics;
            this.material = material;
            this.name = name;
        }
    }

}