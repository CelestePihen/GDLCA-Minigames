package fr.cel.dailyquests.manager.quest;

public record Condition(Jobs job, int amountXp, String condition, int amount) {

    /**
     * Les métiers <br>
     * TODO voir quels métiers
     */
    public enum Jobs {
        HUNTER,
        LUMBERJACK,
        MINER,
    }

}