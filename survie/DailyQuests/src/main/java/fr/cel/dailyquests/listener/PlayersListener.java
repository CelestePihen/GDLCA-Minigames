package fr.cel.dailyquests.listener;

import fr.cel.dailyquests.DailyQuests;
import fr.cel.dailyquests.manager.QPlayer;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.Quest;
import fr.cel.dailyquests.manager.quest.QuestData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class PlayersListener implements Listener {

    private final QuestManager questManager;

    public PlayersListener(DailyQuests main) {
        this.questManager = main.getQuestManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        QPlayer qPlayer = questManager.loadQPlayer(player);

        event.joinMessage(Component.text("[+] ", NamedTextColor.GREEN).append(player.name().color(NamedTextColor.WHITE)));

        questManager.getPlayerData().put(player.getUniqueId(), qPlayer);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Paris"));

        // récupére dernière mise à jour individuelle du joueur
        LocalDateTime lastUpdate = qPlayer.getLastUpdate();

        // si joueur jamais eu quêtes, on les met
        if (lastUpdate == null) {
            qPlayer.renewQuest(Quest.DurationType.DAILY, questManager);
            qPlayer.renewQuest(Quest.DurationType.WEEKLY, questManager);
            qPlayer.setCustomQuest(new QuestData(questManager.getCustomQuest(), 0, LocalDateTime.now()));
            qPlayer.setLastUpdate(now); // On enregistre la première mise à jour
            return;
        }

        // vérifie si passé nouveau jour depuis dernière màj
        if (lastUpdate.toLocalDate().isBefore(now.toLocalDate())) {
            qPlayer.renewQuest(Quest.DurationType.DAILY, questManager);

            // si lundi et joueur pas eu màj cette semaine
            if (now.getDayOfWeek() == DayOfWeek.MONDAY) {
                qPlayer.renewQuest(Quest.DurationType.WEEKLY, questManager);
            }

            player.sendMessage(Component.text("Tes quêtes ont été renouvelées !"));

            // met à jour dernière màj pour joueur
            qPlayer.setLastUpdate(now);
        }

        if (qPlayer.getCustomQuest() == null) {
            qPlayer.setCustomQuest(new QuestData(questManager.getCustomQuest(), 0, LocalDateTime.now()));
        } else if (!qPlayer.getCustomQuest().getQuest().getName().equals(questManager.getCustomQuest().getName())) {
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