package fr.cel.dailyquests;

import fr.cel.dailyquests.command.SeeQuestCommand;
import fr.cel.dailyquests.command.SetCustomCommand;
import fr.cel.dailyquests.listener.PlayersListener;
import fr.cel.dailyquests.listener.QuestListener;
import fr.cel.dailyquests.manager.QuestManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Getter
public final class DailyQuests extends JavaPlugin {

    private QuestManager questManager;

    @Override
    public void onEnable() {
        File playerFolder = new File(getDataFolder(), "players");
        if (!playerFolder.exists()) playerFolder.mkdirs();

        File questsFolder = new File(getDataFolder(), "quests");
        if (!questsFolder.exists()) questsFolder.mkdirs();

        this.questManager = new QuestManager(this);
        scheduleDailyTask();

        for (Player player : Bukkit.getOnlinePlayers()) {
            questManager.getPlayerData().put(player.getUniqueId(), questManager.loadQPlayer(player));
        }

        getServer().getPluginManager().registerEvents(new PlayersListener(this), this);
        getServer().getPluginManager().registerEvents(new QuestListener(questManager), this);

        getCommand("seequest").setExecutor(new SeeQuestCommand(questManager));
        getCommand("setcustom").setExecutor(new SetCustomCommand(questManager));
    }

    /**
     * Lance le chronomètre permettant de détecter si l'on est passé au jour suivant ou pas
     */
    private void scheduleDailyTask() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        LocalDateTime nextMidnight = now.truncatedTo(ChronoUnit.DAYS).plusDays(1);
        long delay = now.until(nextMidnight, ChronoUnit.MILLIS) / 50; // Convertir les millisecondes en ticks (1 tick = 50ms)

        new BukkitRunnable() {
            @Override
            public void run() {
                questManager.renewQuestsForAllPlayers();
                scheduleDailyTask();
            }
        }.runTaskLater(this, delay);
    }

    @Override
    public void onDisable() {
        questManager.savePlayers();
    }

}