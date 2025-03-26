package fr.cel.dailyquests.command;

import fr.cel.dailyquests.manager.QPlayer;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.Quest;
import fr.cel.dailyquests.manager.quest.QuestData;
import fr.cel.dailyquests.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class SeeQuestCommand implements CommandExecutor {

    private final QuestManager questManager;

    public SeeQuestCommand(QuestManager questManager) {
        this.questManager = questManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        QPlayer qPlayer = questManager.getPlayerData().get(player.getUniqueId());
        QuestData dailyQuest = qPlayer.getDailyQuest();
        QuestData weeklyQuest = qPlayer.getWeeklyQuest();
        QuestData customQuest = qPlayer.getCustomQuest();

        Inventory inv = Bukkit.createInventory(null, 9*3, Component.text("Vos quêtes"));

        inv.setItem(11, new ItemBuilder(dailyQuest.quest().material(), dailyQuest.quest().count())
                .setDisplayName(dailyQuest.quest().displayName())
                .setLore(dailyQuest.quest().description(), String.valueOf(dailyQuest.currentAmount()))
                .toItemStack());

        inv.setItem(13, new ItemBuilder(weeklyQuest.quest().material(), weeklyQuest.quest().count())
                .setDisplayName(weeklyQuest.quest().displayName())
                .setLore(weeklyQuest.quest().description(), String.valueOf(weeklyQuest.currentAmount()))
                .toItemStack());

        inv.setItem(15, new ItemBuilder(customQuest.quest().material(), customQuest.quest().count())
                .setDisplayName(customQuest.quest().displayName())
                .setLore(customQuest.quest().description(), String.valueOf(customQuest.currentAmount()))
                .toItemStack());

        player.openInventory(inv);
        return true;
    }

}