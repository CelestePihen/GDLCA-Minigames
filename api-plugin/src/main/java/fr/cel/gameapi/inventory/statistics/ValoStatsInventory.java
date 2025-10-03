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

public class ValoStatsInventory extends AbstractInventory {

    private final Player player;

    public ValoStatsInventory(Player player) {
        super(Component.text("Statistiques - Valocraft"), 27);
        this.player = player;
    }

    @Override
    protected void addItems(Inventory inv) {
        StatisticsManager statisticsManager = GameAPI.getInstance().getStatisticsManager();

        for (ValoStatsInventory.Statistic statistic : ValoStatsInventory.Statistic.values()) {
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
        VALO_GAMES_PLAYED(PlayerStatistics.VALO_GAMES_PLAYED, Material.BOW, Component.text("Parties jouées")),
        VALO_ELIMINATIONS(PlayerStatistics.VALO_ELIMINATIONS, Material.IRON_SWORD, Component.text("Éliminations")),
        VALO_ATTACKER_WINS(PlayerStatistics.VALO_ATTACKER_WINS, Material.TNT, Component.text("Victoires attaquants")),
        VALO_DEFENDER_WINS(PlayerStatistics.VALO_DEFENDER_WINS, Material.SHIELD, Component.text("Victoires défenseurs")),
        VALO_SPIKES_PLANTED(PlayerStatistics.VALO_SPIKES_PLANTED, Material.BREWING_STAND, Component.text("Spikes posés")),
        VALO_SPIKES_DEFUSED(PlayerStatistics.VALO_SPIKES_DEFUSED, Material.BLAZE_POWDER, Component.text("Spikes désamorcés")),
        ;

        private final PlayerStatistics playerStatistics;
        private final Material material;
        private final Component name;

        Statistic(PlayerStatistics playerStatistics, Material material, Component name) {
            this.playerStatistics = playerStatistics;
            this.material = material;
            this.name = name;
        }
    }

}