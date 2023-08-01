package fr.cel.eldenrpg.manager.quest;

import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.quest.quests.KillQuest;
import lombok.Getter;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class QuestManager {

    @Getter private Map<String, Quest> quests;

    public QuestManager(EldenRPG main) {
        quests = new HashMap();
        loadQuests();
    }

    public Quest getQuestById(String id) {
        return quests.get(id);
    }

    private void loadQuests() {
        KillQuest slime = new KillQuest("slime", "Slime", "Tuer les 4 slimes.", EntityType.SLIME, 4);
        quests.put(slime.getId(), slime);
    }


}
