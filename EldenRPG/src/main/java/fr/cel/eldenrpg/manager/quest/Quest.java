package fr.cel.eldenrpg.manager.quest;

import lombok.Getter;

public class Quest {

    @Getter private final String id;
    @Getter private final String displayName;
    @Getter private final String description;

    public Quest(String id, String displayName, String description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }

}