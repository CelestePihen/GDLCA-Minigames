package fr.cel.essentials.commands.other;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HealCommand extends AbstractCommand {

    public HealCommand() {
        super("essentials:heal", false, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0 && sender instanceof Player player) {
            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
            sendMessageWithPrefix(player, "Vous vous êtes soigné(e).");
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (isPlayerOnline(target, sender)) {
                target.setHealth(target.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
                sendMessageWithPrefix(target, "Vous avez été soigné(e).");
                sendMessageWithPrefix(sender, "Vous avez soigné " + target.getName());
            }
        }
        
        else {
            sendMessageWithPrefix(sender, "La commande est : /heal ou /heal <joueur>");
        }

    }

    @Override
    protected List<String> onTabComplete(Player player, String[] strings) {
        return null;
    }

}