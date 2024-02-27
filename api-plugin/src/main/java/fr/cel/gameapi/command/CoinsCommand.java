package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CoinsCommand extends AbstractCommand {

    public CoinsCommand() {
        super("gameapi:coins", false, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        else if (args[0].equalsIgnoreCase("get")) {
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMessageWithPrefix(sender, "Le joueur n'existe pas ou n'est pas connecté.");
                    return;
                }
                sendMessageWithPrefix(sender, "Vous avez " + GameAPI.getInstance().getPlayerManager().getPlayerData(target).getCoins() + " pièce(s)");
            } else {
                sendHelp(sender);
            }
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMessageWithPrefix(sender, "Le joueur n'existe pas ou n'est pas connecté.");
                    return;
                }
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sendMessageWithPrefix(sender, "Merci de mettre un nombre correct.");
                    return;
                }
                GameAPI.getInstance().getPlayerManager().getPlayerData(target).addCoins(amount);
                sendMessageWithPrefix(sender, "Vous avez donné " + amount + " pièce(s) à " + target.getName());
            } else {
                sendHelp(sender);
            }
        }

        else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMessageWithPrefix(sender, "Le joueur n'existe pas ou n'est pas connecté.");
                    return;
                }
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sendMessageWithPrefix(sender, "Merci de mettre un nombre correct.");
                    return;
                }
                GameAPI.getInstance().getPlayerManager().getPlayerData(target).removeCoins(amount);
                sendMessageWithPrefix(sender, "Vous avez retiré " + amount + " pièce(s) à " + target.getName());
            } else {
                sendHelp(sender);
            }
        }

    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ChatUtility.format("[Aide pour les commandes du sytème de coins]", ChatUtility.UtilityColor.GOLD));
        sender.sendMessage("/coins add <pseudo> <montant> : Ajouter des pièces à un joueur");
        sender.sendMessage("/coins remove <pseudo> <montant> : Retirer des pièces à un joueur");
        sender.sendMessage("/coins get <pseudo> : Envoie le nombre de pièces du joueur");
    }

}