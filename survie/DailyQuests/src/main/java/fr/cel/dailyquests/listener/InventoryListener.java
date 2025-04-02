package fr.cel.dailyquests.listener;

import fr.cel.dailyquests.manager.QPlayer;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.QuestData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.view.BrewingStandView;

public final class InventoryListener implements Listener {

    private final QuestManager questManager;

    public InventoryListener(QuestManager questManager) {
        this.questManager = questManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof BrewingStand) {
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && isPotionItem(clickedItem) && hasPotionEffects(clickedItem)) {
                QPlayer qPlayer = questManager.getPlayerData().get(player.getUniqueId());
                if (qPlayer == null) return;

                QuestData dailyQuestData = qPlayer.getDailyQuest();
                if (!dailyQuestData.isFinished()) {
                    dailyQuestData.setCurrentAmount(dailyQuestData.getCurrentAmount() + 1);
                    if (dailyQuestData.isFinished()) {
                        player.sendMessage(Component.text("Vous avez terminé votre quête journalière !"));
                        // TODO ajouter méthode du plugin Métiers pour donner l'xp au joueur
                    }
                }

                QuestData weeklyQuestData = qPlayer.getWeeklyQuest();
                if (!weeklyQuestData.isFinished()) {
                    weeklyQuestData.setCurrentAmount(weeklyQuestData.getCurrentAmount() + 1);
                    if (weeklyQuestData.isFinished()) {
                        player.sendMessage(Component.text("Vous avez terminé votre quête journalière !"));
                        // TODO ajouter méthode du plugin Métiers pour donner l'xp au joueur
                    }
                }
            }
        }

        if (event.getInventory().getType() != InventoryType.CHEST) return;

        String title = event.getView().getTitle();
        if (title.equalsIgnoreCase("Vos quêtes")) {
            event.setCancelled(true);
        }
    }

    private boolean isPotionItem(ItemStack item) {
        Material type = item.getType();
        return type == Material.POTION || type == Material.SPLASH_POTION || type == Material.LINGERING_POTION || type == Material.TIPPED_ARROW;
    }

    private boolean hasPotionEffects(ItemStack item) {
        if (item.getItemMeta() instanceof PotionMeta meta) {
            return !meta.getCustomEffects().isEmpty();
        }
        return false;
    }

}