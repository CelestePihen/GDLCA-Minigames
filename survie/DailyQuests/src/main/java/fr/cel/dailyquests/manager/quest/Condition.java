package fr.cel.dailyquests.manager.quest;

public record Condition(Jobs job, int amountXp, String type, int amount) {

    /**
     * Les métiers <br>
     * TODO voir quels métiers
     */
    public enum Jobs {
        HUNTER,
        MINER,
        FARMER,
        ENCHANTER,
        ALCHEMIST
    }

}