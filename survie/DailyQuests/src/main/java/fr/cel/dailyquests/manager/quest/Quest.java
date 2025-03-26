package fr.cel.dailyquests.manager.quest;

import org.bukkit.Material;

public record Quest(String name, String displayName, String description, Material material, int count,
                    Condition condition, DurationType durationType) {

    /**
     * Les différents types de durée sachant que tout se compte en un (une heure, un jour, une semaine)
     */
    public enum DurationType {
        HOUR,
        DAILY,
        WEEKLY,
        CUSTOM;
    }

}