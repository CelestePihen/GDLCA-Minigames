package fr.cel.eldenrpg.manager.quest.quests;

import fr.cel.eldenrpg.manager.quest.Quest;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.entity.EntityType;

@Getter
public class KillQuest extends Quest {

    private final EntityType target;
    private final int amount;
    @Setter private int progress = 0;

    public KillQuest(String id, String displayName, String description, EntityType target, int amount) {
        super(id, displayName, description);
        this.target = target;
        this.amount = amount;
    }

}