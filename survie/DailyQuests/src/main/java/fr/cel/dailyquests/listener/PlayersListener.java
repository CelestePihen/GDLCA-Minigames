package fr.cel.dailyquests.listener;

import fr.cel.dailyquests.DailyQuests;
import fr.cel.dailyquests.manager.QPlayer;
import fr.cel.dailyquests.manager.quest.Quest;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.QuestData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class PlayersListener implements Listener {

    private final DailyQuests main;
    private final QuestManager questManager;

    public PlayersListener(DailyQuests main) {
        this.main = main;
        this.questManager = main.getQuestManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        QPlayer qPlayer = questManager.loadQPlayer(player);

        if (qPlayer == null) {
            event.joinMessage(Component.empty());
            player.kick(Component.text("Erreur à la connexion... Merci de contacter un admin sur Discord !"));
            return;
        }

        event.joinMessage(Component.text("[+] ", NamedTextColor.GREEN).append(player.name().color(NamedTextColor.WHITE)));

        questManager.getPlayerData().put(player.getUniqueId(), qPlayer);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Paris"));

        // si le joueur a une quête journalière et qu'il y a eu le changement de quête
        if (qPlayer.getDailyQuest() != null) {
            LocalDateTime lastUpdate = qPlayer.getDailyQuest().lastUpdate();
            long hoursSinceLastUpdate = lastUpdate.until(now, ChronoUnit.HOURS);

            if (hoursSinceLastUpdate >= 24) {
                qPlayer.renewQuest(Quest.DurationType.DAILY, questManager);
            }
        }
        // sinon on lui met une quête (nouveau joueur)
        else {
            qPlayer.renewQuest(Quest.DurationType.DAILY, questManager);
        }

        // si le joueur a une quête hebdomadaire et qu'il y a eu le changement de quête
        if (qPlayer.getWeeklyQuest() != null) {
            LocalDateTime lastUpdate = qPlayer.getWeeklyQuest().lastUpdate();
            long hoursSinceLastUpdate = lastUpdate.until(now, ChronoUnit.HOURS);

            if (hoursSinceLastUpdate >= 24*7) {
                qPlayer.renewQuest(Quest.DurationType.WEEKLY, questManager);
            }
        }
        // sinon on lui met une quête (nouveau joueur)
        else {
            qPlayer.renewQuest(Quest.DurationType.WEEKLY, questManager);
        }

        // si le joueur n'a pas la quête custom (nouveau joueur)
        if (qPlayer.getCustomQuest() == null) {
            qPlayer.setCustomQuest(new QuestData(questManager.getCustomQuest(), 0, LocalDateTime.now()));
        }
        // sinon si la quête custom que le joueur a n'est pas la même que celle actuellement alors on la change
        else if (!qPlayer.getCustomQuest().quest().name().equals(questManager.getCustomQuest().name())) {
            qPlayer.setCustomQuest(new QuestData(questManager.getCustomQuest(), 0, LocalDateTime.now()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.quitMessage(Component.text("[-] ", NamedTextColor.RED).append(player.name().color(NamedTextColor.WHITE)));
        if (questManager.getPlayerData().get(player.getUniqueId()) != null) questManager.saveQPlayer(questManager.getPlayerData().get(event.getPlayer().getUniqueId()));
    }

}