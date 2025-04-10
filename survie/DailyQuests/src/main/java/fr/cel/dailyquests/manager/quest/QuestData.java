package fr.cel.dailyquests.manager.quest;

import fr.cel.dailyquests.DailyQuests;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

@Getter
public final class QuestData {

    private final Quest quest;
    @Setter private int currentAmount;
    private final LocalDateTime lastUpdate;

    public QuestData(Quest quest, int currentAmount, LocalDateTime lastUpdate) {
        this.quest = quest;
        this.currentAmount = currentAmount;
        this.lastUpdate = lastUpdate;
    }

    public QuestData(Quest quest, int currentAmount) {
        this(quest, currentAmount, LocalDateTime.now());
    }

    public boolean isFinished() {
        boolean finished = getCurrentAmount() >= getQuest().getCondition().amount();
        if (finished) setCurrentAmount(quest.getCondition().amount());
        return finished;
    }

    public void setFinished(Player player) {
        if (quest.getCondition().job() == Condition.Jobs.MINER) {
            DailyQuests.getInstance().getJobs().getMineurManager().getMineur(player).ajouterExp(player, quest.getCondition().amountXp());
        }
        else if (quest.getCondition().job() == Condition.Jobs.ALCHEMIST) {
            DailyQuests.getInstance().getJobs().getAlchimisteManager().getAlchimiste(player).ajouterExp(player, quest.getCondition().amountXp());
        }
        else if (quest.getCondition().job() == Condition.Jobs.HUNTER) {
            DailyQuests.getInstance().getJobs().getChasseurManager().getChasseur(player).ajouterExp(player, quest.getCondition().amountXp());
        }
        else if (quest.getCondition().job() == Condition.Jobs.ENCHANTER) {
            DailyQuests.getInstance().getJobs().getEnchanteurManager().getEnchanteur(player).ajouterExp(player, quest.getCondition().amountXp());
        }
        else if (quest.getCondition().job() == Condition.Jobs.EXPLORER) {
            DailyQuests.getInstance().getJobs().getExplorateurManager().getExplorateur(player).ajouterExp(player, quest.getCondition().amountXp());
        }
        else if (quest.getCondition().job() == Condition.Jobs.FARMER) {
            DailyQuests.getInstance().getJobs().getFermierManager().getFermier(player).ajouterExp(player, quest.getCondition().amountXp());
        }

        if (quest.getDurationType() == Quest.DurationType.DAILY) {
            player.sendMessage(Component.text("Vous avez terminé votre quête journalière !"));
        }
        else if (quest.getDurationType() == Quest.DurationType.WEEKLY) {
            player.sendMessage(Component.text("Vous avez terminé votre quête hebdomadaire !"));
        }
        else if (quest.getDurationType() == Quest.DurationType.CUSTOM) {
            player.sendMessage(Component.text("Vous avez terminé votre quête personnalisée !"));
        }
    }

}
