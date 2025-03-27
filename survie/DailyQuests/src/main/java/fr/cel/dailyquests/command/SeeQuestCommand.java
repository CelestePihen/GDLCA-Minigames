package fr.cel.dailyquests.command;

import fr.cel.dailyquests.manager.QPlayer;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.QuestData;
import fr.cel.dailyquests.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                    .hideTooltip()
                    .toItemStack());
        }

        inv.setItem(11, new ItemBuilder(dailyQuest.getQuest().material(), dailyQuest.getQuest().count())
                .setDisplayName(dailyQuest.getQuest().displayName())
                .setLore(dailyQuest.getQuest().description(), String.valueOf(dailyQuest.getCurrentAmount()))
                .toItemStack());

        inv.setItem(13, new ItemBuilder(weeklyQuest.getQuest().material(), weeklyQuest.getQuest().count())
                .setDisplayName(weeklyQuest.getQuest().displayName())
                .setLore(weeklyQuest.getQuest().description(), String.valueOf(weeklyQuest.getCurrentAmount()))
                .toItemStack());

        inv.setItem(15, new ItemBuilder(customQuest.getQuest().material(), customQuest.getQuest().count())
                .setDisplayName(customQuest.getQuest().displayName())
                .setLore(customQuest.getQuest().description(), String.valueOf(customQuest.getCurrentAmount()))
                .toItemStack());

        player.openInventory(inv);
        return true;
    }

}