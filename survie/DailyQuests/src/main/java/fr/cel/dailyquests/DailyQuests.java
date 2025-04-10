package fr.cel.dailyquests;

import fr.cel.dailyquests.command.*;
import fr.cel.dailyquests.listener.InventoryListener;
import fr.cel.dailyquests.listener.PlayersListener;
import fr.cel.dailyquests.listener.QuestListener;
import fr.cel.dailyquests.manager.BuildingManager;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.Quest;
import fr.cel.dailyquests.utils.ChatUtility;
import fr.floppa.jobs.Jobs;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Getter
public final class DailyQuests extends JavaPlugin {

    @Getter private static DailyQuests instance;

    @Getter private static final String prefix = ChatUtility.format("[DailyQuests] ", ChatUtility.GOLD);

    @Getter private Jobs jobs;

    private QuestManager questManager;
    private BuildingManager buildingManager;

    private File globalFile;
    private YamlConfiguration globalFileConfig;

    @Override
    public void onEnable() {
        instance = this;

        jobs = (Jobs) Bukkit.getServer().getPluginManager().getPlugin("Jobs");

        if (jobs == null) {
            getSLF4JLogger().error("Impossible de charger le plugin Jobs.");
            Bukkit.shutdown();
        }

        createFolders();
        createGlobalFile();

        this.buildingManager = new BuildingManager(this);
        this.questManager = new QuestManager(this);
        scheduleDailyTask();

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.kick(Component.text("Redémarrage du serveur !")));
    }

    /**
     * Lance le chronomètre permettant de détecter si l'on est passé au jour suivant ou pas
     */
    private void scheduleDailyTask() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            LocalDateTime now = LocalDateTime.now();

            if (now.getHour() == 0 && now.getMinute() == 0 && now.getSecond() == 0) {
                if (now.getDayOfWeek() == DayOfWeek.MONDAY) {
                    questManager.renewQuestsForAllPlayers(Quest.DurationType.WEEKLY, now);
                }
                questManager.renewQuestsForAllPlayers(Quest.DurationType.DAILY, now);
                questManager.setLastUpdate(now);

                getSLF4JLogger().info("Renouvellement des quêtes !");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.showTitle(Title.title(Component.text("Renouvellement des quêtes !"), Component.text("")));
                }
            }
        }, 0L, 20L);
    }

    /**
     * Crée les dossiers requis au fonctionnement du plugin s'ils n'existent pas
     */
    private void createFolders() {
        File playerFolder = new File(getDataFolder(), "players");
        if (!playerFolder.exists()) playerFolder.mkdirs();

        File questsFolder = new File(getDataFolder(), "quests");
        if (!questsFolder.exists()) questsFolder.mkdirs();
    }

    /**
     * Crée le fichier globalFile s'il n'existe pas
     */
    private void createGlobalFile() {
        globalFile = new File(getDataFolder(), "globalFile.yml");
        if (!globalFile.exists()) {
            try {
                globalFile.createNewFile();
            } catch (IOException e) {
                getSLF4JLogger().error("Erreur pour créer le fichier globalFile.yml : {} ", e.getMessage());
            }
        }

        this.globalFileConfig = new YamlConfiguration();
        try {
            this.globalFileConfig.load(globalFile);
        } catch (IOException | InvalidConfigurationException e) {
            getSLF4JLogger().error("Erreur au chargement du fichier globalFile.yml : {}", e.getMessage());
        }
    }

    /**
     * Enregistre les commandes
     */
    private void registerCommands() {
        getCommand("seequest").setExecutor(new SeeQuestCommand(this));
        getCommand("setcustom").setExecutor(new SetCustomCommand(this));
        getCommand("buildingbook").setExecutor(new BuildingBookCommand(buildingManager));
        getCommand("forcequest").setExecutor(new ForceQuestCommand(questManager));

        getCommand("renewquest").setExecutor(new RenewQuestCommand(questManager));
        getCommand("renewquest").setTabCompleter(new RenewQuestCommand(questManager));
    }

    /**
     * Enregistre les événements
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayersListener(this), this);
        getServer().getPluginManager().registerEvents(new QuestListener(questManager), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(questManager), this);
    }

}