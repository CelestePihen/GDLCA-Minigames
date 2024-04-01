package fr.cel.essentials.commands.other;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends AbstractCommand {

    public FlyCommand() {
        super("essentials:fly", true, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                sendMessageWithPrefix(player, "Vous ne pouvez pas faire cette commande en spectateur.");
                return;
            }

            player.setAllowFlight(!player.getAllowFlight());

            if (player.getAllowFlight()) sendMessageWithPrefix(player, "Tu as le fly.");
            else sendMessageWithPrefix(player, "Tu n'as plus le fly.");

            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                if (target.getGameMode().equals(GameMode.SPECTATOR)) {
                    sendMessageWithPrefix(player, "Vous ne pouvez pas faire cette commande si le joueur est en spectateur.");
                    return;
                }

                target.setAllowFlight(!player.getAllowFlight());

                if (target.getAllowFlight()) {
                    sendMessageWithPrefix(target, "Tu as le fly.");
                    sendMessageWithPrefix(player, "Tu as donné le fly à " + target.getName() + ".");
                } else {
                    sendMessageWithPrefix(target, "Tu n'as plus le fly.");
                    sendMessageWithPrefix(player, "Tu as enlevé le fly à " + target.getName() + ".");
                }
            } else {
                sendMessageWithPrefix(player, "Ce joueur n'existe pas ou n'est pas connecté.");
                return;
            }
            return;
        }
        
        if (args.length > 2) {
            sendMessageWithPrefix(player, "La commande est : /fly ou /fly <joueur>");
        }

    }

}
