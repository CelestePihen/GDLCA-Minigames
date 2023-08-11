package fr.cel.hub.commands;

import fr.cel.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HubCommands extends AbstractCommand {

    public HubCommands(Hub main) {
        super(main, "hub", false);
    }

    @Override
    public void onExecute(Player player, String[] args) {
        if (args.length == 0) {
            sendMessageWithPrefix(player, "Vous avez été téléporté(e) au Hub !");
            main.getPlayerManager().sendPlayerToHub(player);
            return;
        }

        if (player.hasPermission("hub.tphub") && args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sendMessageWithPrefix(player, "Ce joueur n'est pas en ligne ou n'existe pas." );
                return;
            }

            main.getPlayerManager().sendPlayerToHub(target);
        }
    }

    @Override
    public void onTabComplete(Player player, String label, String[] args) {}

}