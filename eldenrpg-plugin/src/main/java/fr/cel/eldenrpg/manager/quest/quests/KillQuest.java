package fr.cel.eldenrpg.manager.quest.quests;

import fr.cel.eldenrpg.manager.quest.Quest;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.entity.EntityType;

public class KillQuest extends Quest {

    @Getter private final EntityType target;
    @Getter private final int amount;
    @Getter @Setter private int progress;

    public KillQuest(String id, String displayName, String description, EntityType target, int amount) {
        super(id, displayName, description);
        this.target = target;
        this.amount = amount;
        this.progress = 0;
    }

}