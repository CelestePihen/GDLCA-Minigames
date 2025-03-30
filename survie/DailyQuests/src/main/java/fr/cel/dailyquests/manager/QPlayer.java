package fr.cel.dailyquests.manager;

import fr.cel.dailyquests.manager.quest.Quest;
import fr.cel.dailyquests.manager.quest.QuestData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
public final class QPlayer {

    private final Player player;
    private final UUID uuid;

    @Setter private QuestData dailyQuest;
    @Setter private QuestData weeklyQuest;
    @Setter private QuestData customQuest;

    @Getter @Setter private LocalDateTime lastUpdate;

    public QPlayer(Player player, QuestData dailyQuest, QuestData weeklyQuest, QuestData customQuest) {
        this.player = player;
        this.uuid = player.getUniqueId();

        this.dailyQuest = dailyQuest;
        this.weeklyQuest = weeklyQuest;
        this.customQuest = customQuest;
    }

    public void renewQuest(Quest.DurationType durationType, QuestManager questManager) {
        Quest newQuest = selectNewQuest(durationType, questManager);

        if (durationType == Quest.DurationType.DAILY) {
            setDailyQuest(new QuestData(newQuest, 0, LocalDateTime.now(ZoneId.of("Europe/Paris"))));
        } else if (durationType == Quest.DurationType.WEEKLY) {
            setWeeklyQuest(new QuestData(newQuest, 0, LocalDateTime.now(ZoneId.of("Europe/Paris"))));
        }

        Player player = getPlayer();
        if (player != null) {
            player.sendMessage("Nouvelle quête " + durationType.name().toLowerCase() + " disponible : " + newQuest.getDisplayName());
        }
    }

    private Quest selectNewQuest(Quest.DurationType durationType, QuestManager questManager) {
        List<Quest> quests = new ArrayList<>();

        for (Quest q : questManager.getQuests().values()) {
            if (q.getDurationType() == durationType) quests.add(q);
        }

        return quests.get(new Random().nextInt(quests.size()));
    }

}