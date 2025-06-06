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

public class CCStatsInventory extends AbstractInventory {

    private final Player player;

    /**
     * Constructeur de l'inventaire des statistiques Cache-Cache.
     * @param player Le joueur pour lequel les statistiques sont affichées.
     */
    public CCStatsInventory(Player player) {
        super("Statistiques - Cache-Cache", 27);
        this.player = player;
    }

    @Override
    protected void addItems(Inventory inv) {
        StatisticsManager statisticsManager = GameAPI.getInstance().getStatisticsManager();

        // Tableau des statistiques Cache-Cache à afficher
        StatisticsManager.PlayerStatistics[] stats = {
                StatisticsManager.PlayerStatistics.CC_GAMES_PLAYED,
                StatisticsManager.PlayerStatistics.CC_SEEKER_COUNT,
                StatisticsManager.PlayerStatistics.CC_HIDER_COUNT,
                StatisticsManager.PlayerStatistics.CC_ELIMINATIONS_CC,
                StatisticsManager.PlayerStatistics.CC_BLINDNESS_USAGE,
                StatisticsManager.PlayerStatistics.CC_INVISIBILITY_USAGE,
                StatisticsManager.PlayerStatistics.CC_POINT_PLAYER_USAGE,
                StatisticsManager.PlayerStatistics.CC_SOUND_USAGE,
                StatisticsManager.PlayerStatistics.CC_SPEED_USAGE
        };

        Material[] materials = {
                Material.SPYGLASS, Material.PLAYER_HEAD, Material.LEATHER_HELMET, Material.IRON_SWORD,
                Material.FERMENTED_SPIDER_EYE, Material.GOLDEN_CARROT, Material.FEATHER, Material.NOTE_BLOCK, Material.SUGAR
        };

        String[] names = {
                "&eParties jouées", "&eNombre de fois chercheur", "&eNombre de fois caché", "&eÉliminations",
                "&eUtilisation d'Aveuglement", "&eUtilisation d'Invisibilité", "&eUtilisation de Pointer le joueur", "&eUtilisation de Sons", "&eUtilisation de Vitesse"
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
        if (item.getType() == Material.BARRIER) GameAPI.getInstance().getInventoryManager().openInventory(new StatisticsInventory(), player);
    }

}