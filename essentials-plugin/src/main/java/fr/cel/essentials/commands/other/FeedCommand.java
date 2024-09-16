package fr.cel.essentials.commands.other;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FeedCommand extends AbstractCommand {

    public FeedCommand() {
        super("essentials:feed", false, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0 && sender instanceof Player player) {
            player.setFoodLevel(20);
            sendMessageWithPrefix(player, "Vous vous êtes rassasié(e).");
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (isPlayerOnline(target, sender)) {
                target.setFoodLevel(20);
                sendMessageWithPrefix(target, "Vous avez été rassasié(e).");
                sendMessageWithPrefix(sender, "Vous avez rassasié " + target.getName());
            }
        }
        
        if (args.length > 2) {
            sendMessageWithPrefix(sender, "La commande est : /feed ou /feed <joueur>");
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}