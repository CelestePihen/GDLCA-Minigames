package fr.cel.essentials.commands.other;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class FlyCommand extends AbstractCommand {

    public FlyCommand(Essentials main) {
        super(main, "fly");
    }

    @Override
    protected void onExecute(Player player, String[] args) {

        if (args.length == 0) {
            if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                sendMessageWithPrefix(player, "Vous ne pouvez pas faire cette commande en spectateur.");
                return;
            }

            player.setAllowFlight(!player.getAllowFlight());
            player.setFlying(!player.isFlying());

            if (player.isFlying()) sendMessageWithPrefix(player, "Tu as le fly.");
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
                target.setFlying(!player.isFlying());
                if (target.isFlying()) {
                    sendMessageWithPrefix(target, "Tu as le fly.");
                    sendMessageWithPrefix(player, "Tu as donné le fly à " + target.getName() + ".");
                } else {
                    sendMessageWithPrefix(target, "Tu n'as plus le fly.");
                    sendMessageWithPrefix(player, "Tu as enlevé le fly à " + target.getName() + ".");
                }
                return;
            } else {
                sendMessageWithPrefix(player, "Ce joueur n'existe pas ou n'est pas connecté.");
                return;
            }
        }
        
        if (args.length > 2) {
            sendMessageWithPrefix(player, "La commande est : /fly ou /fly <joueur>");
        }

    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}

}
