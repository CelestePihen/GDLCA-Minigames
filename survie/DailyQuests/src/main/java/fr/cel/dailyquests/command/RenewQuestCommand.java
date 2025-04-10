package fr.cel.dailyquests.command;

import fr.cel.dailyquests.DailyQuests;
import fr.cel.dailyquests.manager.QPlayer;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class RenewQuestCommand implements CommandExecutor, TabCompleter {

    private final QuestManager questManager;

    public RenewQuestCommand(QuestManager questManager) {
        this.questManager = questManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return false;

        if (!player.isOp()) {
            player.sendMessage(DailyQuests.getPrefix() + "Vous devez être opérateur pour utiliser cette commande.");
            return false;
        }

        if (args.length != 2) {
            player.sendMessage(DailyQuests.getPrefix() + "Usage: /renewquest <player> <type>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target != null) {
            String questType = args[1];
            if (questType.equalsIgnoreCase("daily")) {
                QPlayer qTarget = questManager.getPlayerData().get(target.getUniqueId());
                qTarget.renewQuest(Quest.DurationType.DAILY, questManager, false);
            }
            else if (questType.equalsIgnoreCase("weekly")) {
                QPlayer qTarget = questManager.getPlayerData().get(target.getUniqueId());
                qTarget.renewQuest(Quest.DurationType.WEEKLY, questManager, false);
            }
            else {
                player.sendMessage(DailyQuests.getPrefix() + "Les types sont : daily / weekly");
                player.sendMessage(DailyQuests.getPrefix() + "Usage: /renewquest <player> <type>");
            }
        } else {
            player.sendMessage(DailyQuests.getPrefix() + "Usage: /renewquest <player> <type>");
            return false;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) return null;

        if (args.length == 1) {
            return null;
        }

        if (args.length == 2) {
            return List.of("daily", "weekly");
        }

        return null;
    }

}