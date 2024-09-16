package fr.cel.hub.commands;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.command.AbstractCommand;
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
        if (args.length == 0) {
            if (sender instanceof Player player) {
                sendMessageWithPrefix(player, "Vous avez été téléporté(e) au Hub !");
                GameAPI.getInstance().getPlayerManager().sendPlayerToHub(player);
            } else {
                sendMessageWithPrefix(sender, "Vous n'êtes pas un joueur...");
            }
        }

        if (args.length == 1 && sender.isOp()) {
            Player target = Bukkit.getPlayer(args[0]);
            if (isPlayerOnline(target, sender)) {
                GameAPI.getInstance().getPlayerManager().sendPlayerToHub(target);
            }
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}