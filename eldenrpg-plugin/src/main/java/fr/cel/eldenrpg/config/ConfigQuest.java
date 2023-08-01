package fr.cel.eldenrpg.config;

import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.quest.Quest;
import fr.cel.eldenrpg.manager.quest.QuestConfigurationException;
import fr.cel.eldenrpg.manager.quest.quests.ItemQuest;
import fr.cel.eldenrpg.manager.quest.quests.KillQuest;
import fr.cel.eldenrpg.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class ConfigQuest {

    private final String id;
    private final EldenRPG main;

    public ConfigQuest(EldenRPG main, String id) {
        this.id = id;
        this.main = main;
    }

    public Quest getQuest() {
        File file = new File(main.getDataFolder() + File.separator + "quests", id + ".yml");
        if (file.exists()) {
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(file);

                String displayName = config.getString("displayName");
                String description = config.getString("description");

                String type = config.getString("type");

                if (type.equalsIgnoreCase("killentities") && config.getConfigurationSection("entity") != null) {
                    EntityType entityType = EntityType.valueOf(config.getString("entity.entityType").toUpperCase());
                    int amount = config.getInt("entity.amount");

                    return new KillQuest(id, displayName, description, entityType, amount);
                }

                if (type.equalsIgnoreCase("getitem") && config.getConfigurationSection("item") != null) {
                    Material material = Material.valueOf(config.getString("item.material").toUpperCase());
                    int amount = config.getInt("item.amount");
                    String name = config.getString("item.name");

                    ItemStack itemRequired = new ItemBuilder(material, amount).setDisplayName(name).toItemStack();

                    return new ItemQuest(id, displayName, description, itemRequired);
                }

                return null;
            } catch (IOException | InvalidConfigurationException e) {
                throw new QuestConfigurationException("Erreur lors du chargement de la quête " + id, e);
            }
        }

        return null;
    }

}