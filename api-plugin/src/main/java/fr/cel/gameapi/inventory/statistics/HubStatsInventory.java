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

public class HubStatsInventory extends AbstractInventory {

    private final Player player;

    public HubStatsInventory(Player player) {
        super("Statistiques - Hub", 27);
        this.player = player;
    }

    @Override
    protected void addItems(Inventory inv) {
        StatisticsManager statisticsManager = GameAPI.getInstance().getStatisticsManager();

        // Tableau des statistiques Cache-Cache à afficher
        StatisticsManager.PlayerStatistics[] stats = {
                StatisticsManager.PlayerStatistics.HUB_DJ
        };

        Material[] materials = {
                Material.JUKEBOX
        };

        String[] names = {
                "&eParticipation au DJ",
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