package fr.cel.eldenrpg.commands;

import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.player.ERPlayer;
import fr.cel.eldenrpg.manager.quest.Quest;
import fr.cel.eldenrpg.manager.quest.QuestManager;
import org.bukkit.entity.Player;

public class QuestCommand extends AbstractCommand {

    public QuestCommand(EldenRPG main) {
        super(main, "quest");
    }

    // à faire -> /quest <grant/revoke> <joueur> <active/finished/completed> <quest_id>

    // TODO faire la commande au cas où un jour
    @Override
    protected void onExecute(Player player, String[] args) {
        if (args.length <= 1) {
            sendMessageWithPrefix(player, "La commande est : /quest <grand/remove> <quest_id>");
            return;
        }

        ERPlayer erPlayer = main.getPlayerManager().getPlayerData(player.getUniqueId());

        if (erPlayer == null) {
            sendMessageWithPrefix(player, "ERREUR | DITES LE A UN ADMIN SI VOUS VOYEZ CE MESSAGE");
            return;
        }

        if (args.length == 2) {
            Quest quest = main.getQuestManager().getQuests().get(args[1]);
            if (args[0].equalsIgnoreCase("grant")) {
                erPlayer.addActiveQuest(quest);
                sendMessageWithPrefix(player, "La quête " + quest.getDisplayName() + " vous a été donnée.");
            }

            if (args[0].equalsIgnoreCase("revoke")) {
                erPlayer.removeActiveQuest(quest);
                sendMessageWithPrefix(player, "La quête " + quest.getDisplayName() + " vous a été enlevée.");
            }
        }
    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {
        if (args.length == 1) {
            arguments.add("grant");
            arguments.add("revoke");
        }

        if (args.length == 2) {

        }
    }
}
