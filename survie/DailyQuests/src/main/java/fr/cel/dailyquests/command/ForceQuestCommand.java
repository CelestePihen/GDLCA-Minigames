package fr.cel.dailyquests.command;

import fr.cel.dailyquests.DailyQuests;
import fr.cel.dailyquests.manager.QPlayer;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.Quest;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ForceQuestCommand implements CommandExecutor {

    private final QuestManager questManager;

    public ForceQuestCommand(QuestManager questManager) {
        this.questManager = questManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return false;

        if (args.length != 2) {
            player.sendMessage(DailyQuests.getPrefix() + "Usage: /forcequest <player> <type>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target != null) {
            String questType = args[1];
            if (questType.equalsIgnoreCase("daily")) {
                QPlayer qTarget = questManager.getPlayerData().get(target.getUniqueId());
                qTarget.getDailyQuest().setCurrentAmount(qTarget.getDailyQuest().getQuest().getCondition().amount());
                target.sendMessage(Component.text("Vous avez terminé votre quête journalière !"));
            }
            else if (questType.equalsIgnoreCase("weekly")) {
                QPlayer qTarget = questManager.getPlayerData().get(target.getUniqueId());
                qTarget.getWeeklyQuest().setCurrentAmount(qTarget.getWeeklyQuest().getQuest().getCondition().amount());
                target.sendMessage(Component.text("Vous avez terminé votre quête hebdomadaire !"));
            }
            else if (questType.equalsIgnoreCase("custom")) {
                QPlayer qTarget = questManager.getPlayerData().get(target.getUniqueId());
                if (qTarget.getCustomQuest().getQuest().getCustomCompletion() == Quest.CustomCompletion.NO_COMPLETABLE) {
                    qTarget.getCustomQuest().setCurrentAmount(qTarget.getCustomQuest().getQuest().getCondition().amount());
                }
                target.sendMessage(Component.text("Vous avez terminé votre quête personnalisée !"));
            }
            else {
                player.sendMessage(DailyQuests.getPrefix() + "Les types sont : daily / weekly / custom");
                player.sendMessage(DailyQuests.getPrefix() + "Usage: /forcequest <player> <type>");
            }
        } else {
            player.sendMessage(DailyQuests.getPrefix() + "Usage: /forcequest <player> <type>");
            return false;
        }

        return false;
    }

}