package fr.cel.dailyquests.manager;

import fr.cel.dailyquests.DailyQuests;
import fr.cel.dailyquests.manager.quest.Quest;
import fr.cel.dailyquests.manager.quest.QuestConfig;
import fr.cel.dailyquests.manager.quest.QuestData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class QuestManager {

    private final DailyQuests main;

    @Getter private final Map<String, Quest> quests = new HashMap<>();
    @Getter @Setter private Quest customQuest;

    @Getter private final Map<UUID, QPlayer> playerData = new HashMap<>(5);

    @Getter @Setter private LocalDateTime lastUpdate = LocalDateTime.now(ZoneId.of("Europe/Paris"));

    public QuestManager(DailyQuests main) {
        this.main = main;
        loadQuests();

        String customQuestName = main.getGlobalFileConfig().getString("customQuest");
        if (customQuestName != null)
            setCustomQuest(quests.get(customQuestName));
    }

    /**
     * Permet d'obtenir une quête grâce à son nom
     * @param name Le nom de la quête
     * @return Retourne l'instance de quête
     * @see Quest
     */
    public Quest getQuestByName(String name) {
        return this.quests.get(name);
    }

    /**
     * Met à jour les quêtes journalières et hebdomadaires de tous les joueurs connectés
     */
    public void renewQuestsForAllPlayers(Quest.DurationType durationType) {
        for (QPlayer qPlayer : playerData.values()) {
            QuestData dailyQuest = qPlayer.getDailyQuest();
            QuestData weeklyQuest = qPlayer.getWeeklyQuest();

            if (dailyQuest != null && durationType == Quest.DurationType.DAILY) {
                qPlayer.renewQuest(Quest.DurationType.DAILY, this);
            }

            if (weeklyQuest != null && durationType == Quest.DurationType.WEEKLY) {
                qPlayer.renewQuest(Quest.DurationType.WEEKLY, this);
            }
        }
    }

    /**
     * Charge les quêtes d'un joueur quand il rejoint le serveur
     * @param player Instance du joueur
     * @return Retourne une instance de QPlayer
     * @see QPlayer
     */
    public QPlayer loadQPlayer(Player player) {
        final File playerFile = new File(main.getDataFolder() + File.separator + "players", player.getUniqueId() + ".yml");
        if (playerFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            // quête journalière
            String dailyQuestName = config.getString("dailyQuest.name");
            String dailyQuestDate = config.getString("dailyQuest.date");
            int dailyQuestAmount = config.getInt("dailyQuest.amount");

            // quête hebdomadaire
            String weeklyQuestName = config.getString("weeklyQuest.name");
            String weeklyQuestDate = config.getString("weeklyQuest.date");
            int weeklyQuestAmount = config.getInt("weeklyQuest.amount");

            // quête custom
            String customQuestName = config.getString("customQuest.name");
            String customQuestDate = config.getString("customQuest.date");
            int customQuestAmount = config.getInt("customQuest.amount");

            // convertit dates de String à LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dailyQuestLastUpdate = LocalDateTime.parse(dailyQuestDate, formatter);
            LocalDateTime weeklyQuestLastUpdate = LocalDateTime.parse(weeklyQuestDate, formatter);
            LocalDateTime customQuestLastUpdate = LocalDateTime.parse(customQuestDate, formatter);

            // quêtes
            Quest dailyQuest = getQuestByName(dailyQuestName);
            Quest weeklyQuest = getQuestByName(weeklyQuestName);
            Quest customQuest = getQuestByName(customQuestName);

            // QuestData
            QuestData dailyQuestData = new QuestData(dailyQuest, dailyQuestAmount, dailyQuestLastUpdate);
            QuestData weeklyQuestData = new QuestData(weeklyQuest, weeklyQuestAmount, weeklyQuestLastUpdate);
            QuestData customQuestData = new QuestData(customQuest, customQuestAmount, customQuestLastUpdate);

            // Last Update
            String lu = config.getString("lastUpdate");
            LocalDateTime lastUpdate = LocalDateTime.parse(lu, formatter);

            QPlayer qPlayer = new QPlayer(player, dailyQuestData, weeklyQuestData, customQuestData);
            qPlayer.setLastUpdate(lastUpdate);

            return qPlayer;
        } else {
            createPlayerFile(player, playerFile);
            QPlayer qPlayer = new QPlayer(player, null, null, null);
            qPlayer.setLastUpdate(null);
            return qPlayer;
        }
    }

    /**
     * Sauvegarde l'avancée des quêtes du joueur
     * @param qPlayer Instance de QPlayer du joueur
     */
    public void saveQPlayer(QPlayer qPlayer) {
        final File playerFile = new File(main.getDataFolder() + File.separator + "players", qPlayer.getUuid().toString() + ".yml");
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            if (qPlayer.getDailyQuest() != null) {
                config.set("dailyQuest.name", qPlayer.getDailyQuest().getQuest().getName());
                config.set("dailyQuest.date", qPlayer.getDailyQuest().getLastUpdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                config.set("dailyQuest.amount", qPlayer.getDailyQuest().getCurrentAmount());
            }

            if (qPlayer.getWeeklyQuest() != null) {
                config.set("weeklyQuest.name", qPlayer.getWeeklyQuest().getQuest().getName());
                config.set("weeklyQuest.date", qPlayer.getWeeklyQuest().getLastUpdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                config.set("weeklyQuest.amount", qPlayer.getWeeklyQuest().getCurrentAmount());
            }

            if (qPlayer.getCustomQuest() != null) {
                config.set("customQuest.name", qPlayer.getCustomQuest().getQuest().getName());
                config.set("customQuest.date", qPlayer.getCustomQuest().getLastUpdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                config.set("customQuest.amount", qPlayer.getCustomQuest().getCurrentAmount());
            }

            if (qPlayer.getLastUpdate() != null) {
                config.set("lastUpdate", qPlayer.getLastUpdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }

            config.save(playerFile);
        } catch (IOException | NullPointerException e) {
            main.getSLF4JLogger().error("Impossible de sauvegarder le fichier pour {}", qPlayer.getUuid());
        }
    }

    /**
     * Sauvegarde l'avancée des quêtes de tous les joueurs
     */
    public void savePlayers() {
        for (QPlayer qPlayer : this.playerData.values()) saveQPlayer(qPlayer);
    }

    /**
     * Charge les quêtes qui se trouvent dans le dossier quests du plugin
     */
    private void loadQuests() {
        quests.clear();
        File folder = new File(main.getDataFolder(), "quests");
        if (!folder.exists()) folder.mkdirs();
        if (folder.isDirectory()) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                String name = file.getName().replace(".yml", "");
                quests.put(name, new QuestConfig(name, main).getQuest());
                main.getSLF4JLogger().info("Chargement de la quête {}", name);
            }
        }
    }

    /**
     * Créer le fichier du joueur s'il n'existe pas
     * @param player Instance du joueur
     * @param playerFile Fichier du joueur
     */
    private void createPlayerFile(Player player, File playerFile) {
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                main.getSLF4JLogger().error("Impossible de créer le fichier pour {}", player.getUniqueId());
            }
        }
    }

}