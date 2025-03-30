package fr.cel.dailyquests.manager.quest;

import lombok.Getter;
import lombok.Setter;

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
        return getCurrentAmount() == getQuest().getCondition().amount();
    }

}
