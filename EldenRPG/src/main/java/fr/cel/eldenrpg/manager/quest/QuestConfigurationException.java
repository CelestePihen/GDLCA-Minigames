package fr.cel.eldenrpg.manager.quest;

public class QuestConfigurationException extends RuntimeException {

    public QuestConfigurationException(String message) {
        super(message);
    }

    public QuestConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}