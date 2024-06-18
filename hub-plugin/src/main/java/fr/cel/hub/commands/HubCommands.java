package fr.cel.hub.commands;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HubCommands extends AbstractCommand {

    public HubCommands() {
        super("hub:hub", false, false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0 && isPlayer(sender)) {
            sendMessageWithPrefix(player, "Vous avez été téléporté(e) au Hub !");
            GameAPI.getInstance().getPlayerManager().sendPlayerToHub(player);
            return;
        }

        if (args.length == 1 && sender.isOp()) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sendMessageWithPrefix(player, "Ce joueur n'est pas en ligne ou n'existe pas." );
                return;
            }

            GameAPI.getInstance().getPlayerManager().sendPlayerToHub(target);
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}