package fr.cel.essentials.commands.other;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class HealCommand extends AbstractCommand {

    public HealCommand(Essentials main) {
        super(main, "heal");
    }

    @Override
    protected void onExecute(Player player, String[] args) {

        if (args.length == 0) {
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            sendMessageWithPrefix(player, "Vous vous êtes soigné(e).");
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                target.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                sendMessageWithPrefix(target, "Vous avez été soigné(e).");
                sendMessageWithPrefix(player, "Vous avez soigné " + target.getName());
                return;
            } else {
                sendMessageWithPrefix(player, "Ce joueur n'existe pas ou n'est pas connecté.");
                return;
            }
        }
        
        if (args.length > 2) {
            sendMessageWithPrefix(player, "La commande est : /heal ou /heal <joueur>");
        }

    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}

}