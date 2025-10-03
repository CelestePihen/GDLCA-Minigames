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

public class ParkourStatsInventory extends AbstractInventory {

    private final Player player;

    public ParkourStatsInventory(Player player) {
        super(Component.text("Statistiques - Parkour"), 27);
        this.player = player;
    }

    @Override
    protected void addItems(Inventory inv) {
        StatisticsManager statisticsManager = GameAPI.getInstance().getStatisticsManager();

        for (ParkourStatsInventory.Statistic statistic : ParkourStatsInventory.Statistic.values()) {
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
        PARK_JUMPS(PlayerStatistics.PARK_JUMPS, Material.RABBIT_FOOT, Component.text("Sauts effectués")),
        PARK_PARKOURS_COMPLETED(PlayerStatistics.PARK_PARKOURS_COMPLETED, Material.DIAMOND_BOOTS, Component.text("Parkours terminés")),
        PARK_CHECKPOINTS_TAKEN(PlayerStatistics.PARK_CHECKPOINTS_TAKEN, Material.BEACON, Component.text("Checkpoints atteints")),
        ;

        private final StatisticsManager.PlayerStatistics playerStatistics;
        private final Material material;
        private final Component name;

        Statistic(StatisticsManager.PlayerStatistics playerStatistics, Material material, Component name) {
            this.playerStatistics = playerStatistics;
            this.material = material;
            this.name = name;
        }
    }

}