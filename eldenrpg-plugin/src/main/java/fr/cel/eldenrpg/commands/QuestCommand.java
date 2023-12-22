package fr.cel.eldenrpg.commands;

import fr.cel.eldenrpg.EldenRPG;
import fr.cel.eldenrpg.manager.player.ERPlayer;
import fr.cel.eldenrpg.manager.quest.Quest;
import fr.cel.eldenrpg.manager.quest.QuestManager;
import org.bukkit.Bukkit;
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
            player.sendMessage("La commande est : /quest <grand/remove> <quest_id>");
            return;
        }

        ERPlayer erPlayer = main.getPlayerManager().getPlayerData(player.getUniqueId());

        if (erPlayer == null) {
            player.sendMessage("Erreur avec votre profil ! Merci de contacter un administrateur.");
            return;
        }

        if (args.length == 2) {
            Quest quest = main.getQuestManager().getQuests().get(args[1]);
            if (args[0].equalsIgnoreCase("grant")) {
                erPlayer.addActiveQuest(quest);
                player.sendMessage("La quête " + quest.getDisplayName() + " vous a été donnée.");
            }

            if (args[0].equalsIgnoreCase("revoke")) {
                erPlayer.removeActiveQuest(quest);
                player.sendMessage("La quête " + quest.getDisplayName() + " vous a été enlevée.");
            }
        }
    }

    @Override
    protected void onTabComplete(String label, String[] args) {
        if (args.length == 1) {
            arguments.add("grant");
            arguments.add("revoke");
        }

        if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (main.getPlayerManager().getPlayerData(player.getUniqueId()) != null) {
                    arguments.add(player.getName());
                }
            }
        }
    }
}
