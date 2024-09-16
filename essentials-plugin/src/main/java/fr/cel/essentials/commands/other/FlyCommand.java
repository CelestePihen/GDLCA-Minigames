package fr.cel.essentials.commands.other;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FlyCommand extends AbstractCommand {

    public FlyCommand() {
        super("essentials:fly", false,true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0 && sender instanceof Player player) {
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

            if (isPlayerOnline(target, sender)) {
                if (target.getGameMode().equals(GameMode.SPECTATOR)) {
                    sendMessageWithPrefix(sender, "Vous ne pouvez pas faire cette commande si le joueur est en spectateur.");
                    return;
                }

                target.setAllowFlight(!target.getAllowFlight());

                if (target.getAllowFlight()) {
                    sendMessageWithPrefix(target, "Tu as le fly.");
                    sendMessageWithPrefix(sender, "Tu as donné le fly à " + target.getName() + ".");
                } else {
                    sendMessageWithPrefix(target, "Tu n'as plus le fly.");
                    sendMessageWithPrefix(sender, "Tu as enlevé le fly à " + target.getName() + ".");
                }
            }
        }
        
        if (args.length > 2) {
            sendMessageWithPrefix(sender, "La commande est : /fly ou /fly <joueur>");
        }

    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}
