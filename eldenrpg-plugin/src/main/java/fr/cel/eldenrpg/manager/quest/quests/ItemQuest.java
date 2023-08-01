package fr.cel.eldenrpg.manager.quest.quests;

import fr.cel.eldenrpg.manager.quest.Quest;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class ItemQuest extends Quest {

    @Getter private ItemStack itemRequired;

    public ItemQuest(String id, String displayName, String description, ItemStack itemRequired) {
        super(id, displayName, description);
        this.itemRequired = itemRequired;
    }

}