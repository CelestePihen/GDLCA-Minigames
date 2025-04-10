package fr.cel.dailyquests.manager;

import fr.cel.dailyquests.DailyQuests;
import fr.cel.dailyquests.manager.quest.Quest;
import fr.cel.dailyquests.manager.quest.QuestData;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
public final class QPlayer {

    private final Player player;
    private final UUID uuid;

    @Setter private QuestData dailyQuest;
    @Setter private QuestData weeklyQuest;
    @Setter private QuestData customQuest;

    private final List<String> completedDailyQuests;
    private final List<String> completedWeeklyQuests;

    @Setter private boolean hasRenewDaily;
    @Setter private boolean hasRenewWeekly;

    @Setter private LocalDateTime lastUpdate;

    public QPlayer(Player player, QuestData dailyQuest, QuestData weeklyQuest, QuestData customQuest, List<String> completedDailyQuests, List<String> completedWeeklyQuests, boolean hasRenewDaily, boolean hasRenewWeekly) {
        this.player = player;
        this.uuid = player.getUniqueId();

        this.dailyQuest = dailyQuest;
        this.weeklyQuest = weeklyQuest;
        this.customQuest = customQuest;

        this.completedDailyQuests = completedDailyQuests;
        this.completedWeeklyQuests = completedWeeklyQuests;

        this.hasRenewDaily = hasRenewDaily;
        this.hasRenewWeekly = hasRenewWeekly;
    }

    /**
     * Renouvelle la quête selon la durée donné du joueur
     * @param durationType La durée (journalier ou hebdomadaire)
     * @param questManager Le gestionnaire de quêtes
     */
    public void renewQuest(Quest.DurationType durationType, QuestManager questManager, boolean isPlayer) {
        Quest newQuest = selectNewQuest(durationType, questManager);

        if (durationType == Quest.DurationType.DAILY) {
            if (isPlayer && !hasRenewDaily) {
                setDailyQuest(new QuestData(newQuest, 0, LocalDateTime.now(ZoneId.of("Europe/Paris"))));
                completedDailyQuests.add(newQuest.getName());
                player.sendMessage(Component.text("Nouvelle quête journalière disponible : " + newQuest.getDisplayName()));
                hasRenewDaily = true;
            } else if (!isPlayer) {
                setDailyQuest(new QuestData(newQuest, 0, LocalDateTime.now(ZoneId.of("Europe/Paris"))));
                completedDailyQuests.add(newQuest.getName());
                player.sendMessage(Component.text("Nouvelle quête journalière disponible : " + newQuest.getDisplayName()));
            }
        } else if (durationType == Quest.DurationType.WEEKLY) {
            if (isPlayer && !hasRenewWeekly) {
                setWeeklyQuest(new QuestData(newQuest, 0, LocalDateTime.now(ZoneId.of("Europe/Paris"))));
                completedWeeklyQuests.add(newQuest.getName());
                player.sendMessage(Component.text("Nouvelle quête hebdomadaire disponible : " + newQuest.getDisplayName()));
                hasRenewWeekly = true;
            } else if (!isPlayer) {
                setWeeklyQuest(new QuestData(newQuest, 0, LocalDateTime.now(ZoneId.of("Europe/Paris"))));
                completedWeeklyQuests.add(newQuest.getName());
                player.sendMessage(Component.text("Nouvelle quête hebdomadaire disponible : " + newQuest.getDisplayName()));
            }
        }
    }

    /**
     * Sauvegarde l'avancée des quêtes du joueur
     */
    public void saveQPlayer() {
        final File playerFile = new File(DailyQuests.getInstance().getDataFolder() + File.separator + "players", getUuid().toString() + ".yml");
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            if (getDailyQuest() != null) {
                config.set("dailyQuest.name", getDailyQuest().getQuest().getName());
                config.set("dailyQuest.date", getDailyQuest().getLastUpdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                config.set("dailyQuest.amount", getDailyQuest().getCurrentAmount());
                config.set("dailyQuest.renew", hasRenewDaily);
            }

            if (getWeeklyQuest() != null) {
                config.set("weeklyQuest.name", getWeeklyQuest().getQuest().getName());
                config.set("weeklyQuest.date", getWeeklyQuest().getLastUpdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                config.set("weeklyQuest.amount", getWeeklyQuest().getCurrentAmount());
                config.set("weeklyQuest.renew", hasRenewWeekly);
            }

            if (getCustomQuest() != null) {
                config.set("customQuest.name", getCustomQuest().getQuest().getName());
                config.set("customQuest.date", getCustomQuest().getLastUpdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                config.set("customQuest.amount", getCustomQuest().getCurrentAmount());
            }

            if (getLastUpdate() != null) {
                config.set("lastUpdate", getLastUpdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }

            if (!completedDailyQuests.isEmpty()) {
                config.set("completedDailyQuests", new ArrayList<>(completedDailyQuests));
            }

            if (!completedWeeklyQuests.isEmpty()) {
                config.set("completedWeeklyQuests", new ArrayList<>(completedWeeklyQuests));
            }

            config.save(playerFile);
        } catch (IOException | NullPointerException e) {
            DailyQuests.getInstance().getSLF4JLogger().error("Impossible de sauvegarder le fichier pour {} - {}", getPlayer().getName(), getUuid());
        }
    }

    /**
     * Sélectionne une nouvelle quête que le joueur n'a pas encore eu
     * @param durationType La durée (journalier ou hebdomadaire)
     * @param questManager Le gestionnaire de quêtes
     * @return Retourne une instance de la {@link Quest} sélectionnée
     * @see fr.cel.dailyquests.manager.quest.Quest.DurationType
     */
    private Quest selectNewQuest(Quest.DurationType durationType, QuestManager questManager) {
        List<Quest> availableQuests = new ArrayList<>();
        List<String> completedQuests = (durationType == Quest.DurationType.DAILY) ? completedDailyQuests : completedWeeklyQuests;

        for (Quest q : questManager.getQuests().values()) {
            if (q.getDurationType() == durationType && !completedQuests.contains(q.getName())) availableQuests.add(q);
        }

        if (availableQuests.isEmpty()) {
            completedQuests.clear();
            for (Quest q : questManager.getQuests().values()) {
                if (q.getDurationType() == durationType) availableQuests.add(q);
            }
        }

        return availableQuests.get(new Random().nextInt(availableQuests.size()));
    }

}