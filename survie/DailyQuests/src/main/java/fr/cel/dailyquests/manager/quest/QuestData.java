package fr.cel.dailyquests.manager.quest;

import java.time.LocalDateTime;

public record QuestData(Quest quest, int currentAmount, LocalDateTime lastUpdate) {

    public Quest.DurationType getDurationType() {
        return quest().durationType();
    }

}
