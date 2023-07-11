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
                player.sendMessage(main.getPrefix() + "Vous n'êtes plus en mode invulnérable.");
                return;
            } else {
                main.getPlayersInGod().add(player.getUniqueId());
                player.sendMessage(main.getPrefix() + "Vous êtes en mode invulnérable.");
                return;
            }
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null) {
                if (main.containsPlayersInGod(target)) {
                    main.getPlayersInGod().remove(target.getUniqueId());
                    target.sendMessage(main.getPrefix() + "Vous n'êtes plus en mode invulnérable.");
                    player.sendMessage(main.getPrefix() + "Vous avez enlevé le mode invulnérable de " + target.getName() + ".");
                    return;
                } else {
                    main.getPlayersInGod().add(player.getUniqueId());
                    target.sendMessage(main.getPrefix() + "Vous avez été mis(e) en mode invulnérable.");
                    player.sendMessage(main.getPrefix() + "Vous avez mis en mode invulnérable " + target.getName() + ".");
                    return;
                }
            } else {
                player.sendMessage(main.getPrefix() + "Ce joueur n'existe pas ou n'est pas connecté.");
                return;
            }

        }

    }
    
}
