package fr.cel.dailyquests.command;

import fr.cel.dailyquests.manager.QPlayer;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.QuestData;
import fr.cel.dailyquests.utils.ChatUtility;
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
            inv.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).hideTooltip().toItemStack());
        }

        inv.setItem(11, new ItemBuilder(dailyQuest.getQuest().getMaterial(), dailyQuest.getQuest().getCount())
                .setDisplayName(dailyQuest.getQuest().getDisplayName())
                .setLore(ChatUtility.format(dailyQuest.getQuest().getDescription(), ChatUtility.WHITE),
                        ChatUtility.format(dailyQuest.getCurrentAmount() + "/" + dailyQuest.getQuest().getCondition().amount(), ChatUtility.WHITE))
                .toItemStack());

        inv.setItem(13, new ItemBuilder(weeklyQuest.getQuest().getMaterial(), weeklyQuest.getQuest().getCount())
                .setDisplayName(weeklyQuest.getQuest().getDisplayName())
                .setLore(ChatUtility.format(weeklyQuest.getQuest().getDescription(), ChatUtility.WHITE),
                        ChatUtility.format(weeklyQuest.getCurrentAmount()+ "/" + dailyQuest.getQuest().getCondition().amount(), ChatUtility.WHITE))
                .toItemStack());

        if (customQuest.getQuest() != null) {
            inv.setItem(15, new ItemBuilder(customQuest.getQuest().getMaterial(), customQuest.getQuest().getCount())
                    .setDisplayName(customQuest.getQuest().getDisplayName())
                    .setLore(ChatUtility.format(customQuest.getQuest().getDescription(), ChatUtility.WHITE))
                    .toItemStack());
        } else {
            inv.setItem(15, new ItemBuilder(Material.BARRIER).setDisplayName("Aucune quête communautaire n'est activée.").toItemStack());
        }

        player.openInventory(inv);
        return true;
    }

}