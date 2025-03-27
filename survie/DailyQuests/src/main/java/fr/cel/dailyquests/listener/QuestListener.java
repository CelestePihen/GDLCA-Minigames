package fr.cel.dailyquests.listener;

import fr.cel.dailyquests.manager.QPlayer;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.Condition;
import fr.cel.dailyquests.manager.quest.Quest;
import fr.cel.dailyquests.manager.quest.QuestData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class QuestListener implements Listener {

    private final QuestManager questManager;

    public QuestListener(QuestManager questManager) {
        this.questManager = questManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        QPlayer qPlayer = questManager.getPlayerData().get(player.getUniqueId());

        if (qPlayer == null) return;

        QuestData dailyQuestData = qPlayer.getDailyQuest();
        QuestData weeklyQuestData = qPlayer.getWeeklyQuest();

        Quest dailyQuest = dailyQuestData.getQuest();
        Quest weeklyQuest = weeklyQuestData.getQuest();

        // vérif pour quête journalière
        if (!dailyQuestData.isFinished() && dailyQuest.condition().job() == Condition.Jobs.MINER) {
            if (Material.valueOf(dailyQuest.condition().condition()) == event.getBlock().getType()) {
                dailyQuestData.setCurrentAmount(dailyQuestData.getCurrentAmount() + 1);
                if (dailyQuestData.isFinished()) {
                    player.sendMessage(Component.text("Vous avez terminé votre quête journalière !"));
                    // TODO ajouter méthode du plugin Métiers pour donner l'xp au joueur
                }
            }
        }

        // vérif pour quête hebdomadaire
        if (!weeklyQuestData.isFinished() && weeklyQuest.condition().job() == Condition.Jobs.MINER) {
            if (Material.valueOf(weeklyQuest.condition().condition()) == event.getBlock().getType()) {
                weeklyQuestData.setCurrentAmount(weeklyQuestData.getCurrentAmount() + 1);
                if (weeklyQuestData.isFinished()) {
                    player.sendMessage(Component.text("Vous avez terminé votre quête hebdomadaire !"));
                    // TODO ajouter méthode du plugin Métiers pour donner l'xp au joueur
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        QPlayer qPlayer = questManager.getPlayerData().get(player.getUniqueId());
        if (qPlayer == null) return;

        QuestData dailyQuestData = qPlayer.getDailyQuest();
        QuestData weeklyQuestData = qPlayer.getWeeklyQuest();

        Quest dailyQuest = dailyQuestData.getQuest();
        Quest weeklyQuest = weeklyQuestData.getQuest();

        // vérif pour quête journalière
        if (!dailyQuestData.isFinished() && dailyQuest.condition().job() == Condition.Jobs.HUNTER) {
            if (EntityType.valueOf(dailyQuest.condition().condition()) == event.getEntity().getType()) {
                dailyQuestData.setCurrentAmount(dailyQuestData.getCurrentAmount() + 1);
                if (dailyQuestData.isFinished()) {
                    player.sendMessage(Component.text("Vous avez terminé votre quête journalière !"));
                    // TODO ajouter méthode du plugin Métiers pour donner l'xp au joueur
                }
            }
        }

        // vérif pour quête hebdomadaire
        else if (!weeklyQuestData.isFinished() && weeklyQuest.condition().job() == Condition.Jobs.HUNTER) {
            if (EntityType.valueOf(weeklyQuest.condition().condition()) == event.getEntity().getType()) {
                weeklyQuestData.setCurrentAmount(weeklyQuestData.getCurrentAmount() + 1);
                if (weeklyQuestData.isFinished()) {
                    player.sendMessage(Component.text("Vous avez terminé votre quête hebdomadaire !"));
                    // TODO ajouter méthode du plugin Métiers pour donner l'xp au joueur
                }
            }
        }
    }


}
