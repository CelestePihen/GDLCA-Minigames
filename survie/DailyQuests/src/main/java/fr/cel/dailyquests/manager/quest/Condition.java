package fr.cel.dailyquests.manager.quest;

public record Condition(Jobs job, int amountXp, String type, int amount) {

    public enum Jobs {
        HUNTER,
        MINER,
        FARMER,
        ENCHANTER,
        ALCHEMIST,
        EXPLORER
    }

}