package fr.cel.gameapi.inventory.statistics;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.AbstractInventory;
import fr.cel.gameapi.inventory.StatisticsInventory;
import fr.cel.gameapi.manager.database.StatisticsManager;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.gameapi.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ValoStatsInventory extends AbstractInventory {

    private final Player player;

    public ValoStatsInventory(Player player) {
        super("Statistiques - Valocraft", 27);
        this.player = player;
    }

    @Override
    protected void addItems(Inventory inv) {
        StatisticsManager statisticsManager = GameAPI.getInstance().getStatisticsManager();

        // Tableau des statistiques Cache-Cache à afficher
        StatisticsManager.PlayerStatistics[] stats = {
                StatisticsManager.PlayerStatistics.VALO_GAMES_PLAYED,
                StatisticsManager.PlayerStatistics.VALO_ELIMINATIONS,
                StatisticsManager.PlayerStatistics.VALO_ATTACKER_WINS,
                StatisticsManager.PlayerStatistics.VALO_DEFENDER_WINS,
                StatisticsManager.PlayerStatistics.VALO_SPIKES_PLANTED,
                StatisticsManager.PlayerStatistics.VALO_SPIKES_DEFUSED
        };

        Material[] materials = {
                Material.BOW,
                Material.IRON_SWORD,
                Material.TNT,
                Material.SHIELD,
                Material.BREWING_STAND,
                Material.BLAZE_POWDER
        };

        String[] names = {
                "&eParties jouées",
                "&eÉliminations",
                "&eVictoires attaquants",
                "&eVictoires défenseurs",
                "&eSpikes posés",
                "&eSpikes désamorcés"
        };

        for (int i = 0; i < stats.length; i++) {
            inv.setItem(i, new ItemBuilder(materials[i])
                    .setItemName(names[i])
                    .setLore(ChatUtility.format("&7Valeur : &a" + statisticsManager.getPlayerStatistic(player, stats[i])))
                    .toItemStack());
        }

        inv.setItem(22, new ItemBuilder(Material.BARRIER)
                .setItemName("&cFermer")
                .toItemStack());
    }

    @Override
    public void interact(Player player, String itemName, ItemStack item) {
        if (item.getType() == Material.BARRIER) GameAPI.getInstance().getInventoryManager().openInventory(new StatisticsInventory(this.player), player);
    }

}