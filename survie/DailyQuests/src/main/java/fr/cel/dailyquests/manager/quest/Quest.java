package fr.cel.dailyquests.manager.quest;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public final class Quest {

    private final String name;
    private final String displayName;
    private final String description;
    private final Material material;
    private final int count;
    private final Condition condition;
    private final DurationType durationType;

    // Only for Custom Quest
    private final CustomCompletion customCompletion;

    // Daily and Weekly Quest
    public Quest(String name, String displayName, String description, Material material, int count, Condition condition, DurationType durationType) {
        this(name, displayName, description, material, count, condition, durationType, null);
    }

    // Custom Quest
    public Quest(String name, String displayName, String description, Material material, int count, CustomCompletion customCompletion) {
        this(name, displayName, description, material, count, null, null, customCompletion);
    }

    private Quest(String name, String displayName, String description, Material material, int count, Condition condition, DurationType durationType, CustomCompletion customCompletion) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.count = count;
        this.condition = condition;
        this.durationType = durationType;
        this.customCompletion = customCompletion;
    }

    /**
     * Les différents types de durée sachant que tout se compte en un (une heure, un jour, une semaine)
     */
    public enum DurationType {
        HOUR,
        DAILY,
        WEEKLY,
        CUSTOM;
    }

    public enum CustomCompletion {
        NO_COMPLETABLE,
        CHESTS
    }

}