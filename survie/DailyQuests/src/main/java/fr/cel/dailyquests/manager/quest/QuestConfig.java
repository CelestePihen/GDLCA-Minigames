package fr.cel.dailyquests.manager.quest;

import fr.cel.dailyquests.DailyQuests;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class QuestConfig {

    private final DailyQuests main;
    private final String questName;

    public QuestConfig(String questName, DailyQuests main) {
        this.questName = questName;
        this.main = main;
    }

    /**
     * Cette fonction permet de convertir un fichier yml en une instance de quête
     * @return Retourne une instance de quête
     * @see Quest
     * @throws IOException Retourne une IOException si on n'arrive pas à accéder au fichier
     * @throws InvalidConfigurationException Retourne une InvalidConfigurationException s'il y a un problème dans la configuration
     * @throws NullPointerException Retourne un NullPointerException si le fichier n'existe pas, n'est pas trouvé ou si ce n'est pas un fichier de quête
     */
    public Quest getQuest() {
        File file = new File(main.getDataFolder() + File.separator + "quests", questName + ".yml");
        if (file.exists()) {
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(file);

                if (!config.contains("displayName")) {
                    main.getSLF4JLogger().info("Attention ! Le fichier {} ne contient pas la valeur displayName", questName);
                    return null;
                }

                if (!config.contains("durationType")) {
                    main.getSLF4JLogger().info("Attention ! Le fichier {} ne contient pas la valeur durationType", questName);
                    return null;
                }

                Quest.DurationType durationType = Quest.DurationType.valueOf(config.getString("durationType"));

                if (durationType == Quest.DurationType.CUSTOM) {
                    if (!config.contains("completion")) {
                        main.getSLF4JLogger().info("Attention ! Il manque la catégorie completion dans la quête custom {}", questName);
                        return null;
                    }

                    return new Quest(
                            questName, config.getString("displayName"),
                            config.getString("description"),
                            Material.valueOf(config.getString("material")), config.getInt("count"),
                            Quest.CustomCompletion.valueOf(config.getString("completion"))
                    );
                }

                if (!config.contains("job")) {
                    main.getSLF4JLogger().info("Attention ! Il manque la catégorie job dans la quête {}", questName);
                    return null;
                }

                return new Quest(
                        questName, config.getString("displayName"),
                        config.getString("description"),
                        Material.valueOf(config.getString("material")), config.getInt("count"),
                        new Condition(
                                Condition.Jobs.valueOf(config.getString("job.name")), config.getInt("job.amountXp"),
                                config.getString("job.type"), config.getInt("job.amount")),
                        durationType
                );
            } catch (IOException | InvalidConfigurationException | NullPointerException e) {
                main.getSLF4JLogger().error(e.getMessage());
            }
        }
        return null;
    }

}