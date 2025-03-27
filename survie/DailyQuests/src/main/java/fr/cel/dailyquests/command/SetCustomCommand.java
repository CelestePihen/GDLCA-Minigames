package fr.cel.dailyquests.command;

import fr.cel.dailyquests.manager.QuestManager;
import fr.cel.dailyquests.manager.quest.Quest;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetCustomCommand implements CommandExecutor {

    private final QuestManager questManager;

    public SetCustomCommand(QuestManager questManager) {
        this.questManager = questManager;
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
            sender.sendMessage(Component.text("La quête custom a été mise à jour ! Nouvelle quête : " + customQuestName));
        } else {
            sender.sendMessage(Component.text("La commande est : /setcustom <nom_quete_custom>"));
            return false;
        }

        return true;
    }

}