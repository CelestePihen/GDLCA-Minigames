package fr.cel.essentials.commands.other;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand extends AbstractCommand {

    public FeedCommand() {
        super("essentials:feed", true, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            player.setFoodLevel(20);
            sendMessageWithPrefix(player, "Vous vous êtes rassasié(e).");
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                target.setFoodLevel(20);
                sendMessageWithPrefix(target, "Vous avez été rassasié(e).");
                sendMessageWithPrefix(player, "Vous avez rassasié " + target.getName());
            } else {
                sendMessageWithPrefix(player, "Ce joueur n'existe pas ou n'est pas connecté.");
            }
        }
        
        if (args.length > 2) {
            sendMessageWithPrefix(player, "La commande est : /feed ou /feed <joueur>");
        }
    }

}