package fr.cel.dailyquests.command;

import fr.cel.dailyquests.DailyQuests;
import fr.cel.dailyquests.manager.QPlayer;
import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.Quest;
import fr.cel.dailyquests.manager.quest.QuestData;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class SetCustomCommand implements CommandExecutor {

    private final DailyQuests main;
    private final QuestManager questManager;

    public SetCustomCommand(DailyQuests main) {
        this.main = main;
        this.questManager = main.getQuestManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player player && !player.isOp()) {
            player.sendMessage(Component.text("Vous ne pouvez pas faire cette commande."));
            return false;
        }

        if (args.length == 1) {
            String customQuestName = args[0];
            Quest customQuest = questManager.getQuestByName(customQuestName);

            if (customQuest == null) {
                sender.sendMessage(Component.text("La quête custom " + customQuestName + " n'existe pas."));
                return false;
            }

            questManager.setCustomQuest(customQuest);

            for (QPlayer qPlayer : questManager.getPlayerData().values()) {
                qPlayer.setCustomQuest(new QuestData(customQuest, 0));
            }

            try {
                main.getGlobalFileConfig().set("customQuest", customQuestName);
                main.getGlobalFileConfig().save(main.getGlobalFile());
            } catch (IOException e) {
                main.getSLF4JLogger().error("Impossible de sauvegarder la customQuest avec la commande setcustom : {}", e.getMessage());
            }

            sender.sendMessage(Component.text("La quête custom a été mise à jour ! Nouvelle quête : " + customQuestName));
        } else {
            sender.sendMessage(Component.text("La commande est : /setcustom <quete_custom>"));
            return false;
        }

        return true;
    }

}