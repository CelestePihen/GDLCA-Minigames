package fr.cel.essentials.commands.other;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class FeedCommand extends AbstractCommand {

    public FeedCommand(Essentials main) {
        super(main, "feed");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        
        if (args.length <= 0) {
            player.setFoodLevel(20);
            player.sendMessage(main.getPrefix() + "Vous vous êtes rassasié(e).");
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                target.setFoodLevel(20);
                target.sendMessage(main.getPrefix() + "Vous avez été rassasié(e).");
                player.sendMessage(main.getPrefix() + "Vous avez rassasié " + target.getName());
                return;
            } else {
                player.sendMessage(main.getPrefix() + "Ce joueur n'existe pas ou n'est pas connecté.");
                return;
            }
        }
        
        if (args.length > 2) {
            player.sendMessage(main.getPrefix() + "La commande est : /feed ou /feed <joueur>");
            return;
        }

    }
    
}