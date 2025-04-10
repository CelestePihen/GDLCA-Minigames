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

public final class PlayersListener implements Listener {

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

        // si joueur a jamais eu de quêtes, on les met
        if (lastUpdate == null) {
            qPlayer.renewQuest(Quest.DurationType.DAILY, questManager, false);
            qPlayer.renewQuest(Quest.DurationType.WEEKLY, questManager, false);
            qPlayer.setCustomQuest(new QuestData(questManager.getCustomQuest(), 0, LocalDateTime.now()));
            qPlayer.setLastUpdate(now); // enregistre première mise à jour
            return;
        }

        // vérifie si passé nouveau jour depuis dernière màj
        if (lastUpdate.toLocalDate().isBefore(now.toLocalDate())) {
            qPlayer.renewQuest(Quest.DurationType.DAILY, questManager, false);
            qPlayer.setHasRenewDaily(false);

            // si lundi et joueur a pas eu de màj cette semaine
            if (now.getDayOfWeek() == DayOfWeek.MONDAY) {
                qPlayer.renewQuest(Quest.DurationType.WEEKLY, questManager, false);
                qPlayer.setHasRenewWeekly(false);
            }

            player.sendMessage(Component.text("Tes quêtes ont été renouvelées !"));

            // met à jour dernière màj pour joueur
            qPlayer.setLastUpdate(now);
        }

        if (qPlayer.getCustomQuest() == null || !qPlayer.getCustomQuest().getQuest().getName().equals(questManager.getCustomQuest().getName())) {
            qPlayer.setCustomQuest(new QuestData(questManager.getCustomQuest(), 0, LocalDateTime.now()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.quitMessage(Component.text("[-] ", NamedTextColor.RED).append(player.name().color(NamedTextColor.WHITE)));
        if (questManager.getPlayerData().get(player.getUniqueId()) != null) questManager.getPlayerData().get(event.getPlayer().getUniqueId()).saveQPlayer();
    }

}