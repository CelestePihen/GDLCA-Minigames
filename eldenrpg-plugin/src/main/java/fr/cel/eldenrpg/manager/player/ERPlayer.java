package fr.cel.eldenrpg.manager.player;

import fr.cel.eldenrpg.manager.quest.Quest;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;

public class ERPlayer {

    @Getter private final Player player;
    @Getter private final UUID playerUUID;

    @Getter @Setter private Set<Quest> activeQuests;
    @Getter @Setter private Set<Quest> finishedQuests;
    @Getter @Setter private Set<Quest> completedQuests;

    public ERPlayer(Player player) {
        this.player = player;
        this.playerUUID = player.getUniqueId();

        this.activeQuests = new HashSet<>();
        this.completedQuests = new HashSet<>();
        this.finishedQuests = new HashSet<>();
    }

    /**
     * Permet d'ajouter la quête active
     * @param quest La quête active à ajouter
     */
    public void addActiveQuest(Quest quest) {
        activeQuests.add(quest);
    }

    /**
     * Permet de retirer la quête active
     * @param quest La quête active à retirer
     */
    public void removeActiveQuest(Quest quest) {
        activeQuests.remove(quest);
    }

    /**
     * Permet de vérifier si le joueur a la quête active
     * @param quest La quête active à vérifier
     */
    public boolean hasActiveQuest(Quest quest) {
        return activeQuests.contains(quest);
    }

    /**
     * Permet d'ajouter la quête active
     * @param quest La quête active à ajouter
     */
    public void addFinishedQuest(Quest quest) {
        finishedQuests.add(quest);
    }

    /**
     * Permet de retirer la quête active
     * @param quest La quête active à retirer
     */
    public void removeFinishedQuest(Quest quest) {
        finishedQuests.remove(quest);
    }

    /**
     * Permet de vérifier si le joueur a la quête active
     * @param quest La quête active à vérifier
     */
    public boolean hasFinishedQuest(Quest quest) {
        return finishedQuests.contains(quest);
    }

    /**
     * Permet d'ajouter une nouvelle quête complétée
     * @param quest La quête complétée à ajouter
     */
    public void addCompletedQuest(Quest quest) {
        completedQuests.add(quest);
    }

    /**
     * Permet de retirer une quête complétée. Est-ce que cela servira à quelque chose ? Je pense pas
     * @param quest La quête complétée à retirer
     */
    public void removeCompletedQuest(Quest quest) {
        completedQuests.remove(quest);
    }

    /**
     * Permet de vérifier si le joueur a déjà complété cette quête
     * @param quest La quête complétée à vérifier
     */
    public boolean hasCompletedQuest(Quest quest) {
        return completedQuests.contains(quest);
    }

    /**
     * Permet de passer une quête active à une quête finie
     * @param quest La quête
     */
    public void activeToFinished(Quest quest) {
        if (!hasActiveQuest(quest)) return;
        if (hasFinishedQuest(quest)) return;

        removeActiveQuest(quest);
        addFinishedQuest(quest);
    }

    /**
     * Permet de passer une quête finie à une quête complétée
     * @param quest La quête
     */
    public void finishedToCompleted(Quest quest) {
        if (!hasFinishedQuest(quest)) return;
        if (hasCompletedQuest(quest)) return;

        removeFinishedQuest(quest);
        addCompletedQuest(quest);

    }

    /**
     * Permet de passer de quête active à une quête complétée
     * @param quest La quête
     */
    public void activeToCompleted(Quest quest) {
        removeActiveQuest(quest);
        addCompletedQuest(quest);
    }

    // Astuces / Hints
    @Getter @Setter private boolean hFirstFirecampActivated = false;
    @Getter @Setter private boolean hPassThroughBlockActivated = false;

}