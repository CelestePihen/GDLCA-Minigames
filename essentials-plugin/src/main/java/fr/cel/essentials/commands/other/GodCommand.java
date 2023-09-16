package fr.cel.essentials.commands.other;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class GodCommand extends AbstractCommand {

    public GodCommand(Essentials main) {
        super(main, "god");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        
        if (args.length == 0) {
            if (main.containsPlayersInGod(player)) {
                main.getPlayersInGod().remove(player.getUniqueId());
                sendMessageWithPrefix(player, "Vous n'êtes plus en mode invulnérable.");
            } else {
                main.getPlayersInGod().add(player.getUniqueId());
                sendMessageWithPrefix(player, "Vous êtes en mode invulnérable.");
            }
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                if (main.containsPlayersInGod(target)) {
                    main.getPlayersInGod().remove(target.getUniqueId());
                    sendMessageWithPrefix(target, "Vous n'êtes plus en mode invulnérable.");
                    sendMessageWithPrefix(player, "Vous avez enlevé le mode invulnérable de " + target.getName() + ".");
                } else {
                    main.getPlayersInGod().add(player.getUniqueId());
                    sendMessageWithPrefix(target, "Vous avez été mis(e) en mode invulnérable.");
                    sendMessageWithPrefix(player, "Vous avez mis en mode invulnérable " + target.getName() + ".");
                }
            } else {
                sendMessageWithPrefix(player, "Ce joueur n'existe pas ou n'est pas connecté.");
            }

        }

    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}

}
