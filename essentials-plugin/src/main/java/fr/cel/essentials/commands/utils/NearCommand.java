package fr.cel.essentials.commands.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class NearCommand extends AbstractCommand {

    public NearCommand(Essentials main) {
        super(main, "near");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        
        if (args.length == 0 || args.length > 3) {
            player.sendMessage(main.getPrefix() + "La commande est : /near <radius> ou /near <radius> <joueur>");
            return;
        }

        if (args.length == 1) {
            searchPlayers(player, player, args);
            return;

        }

        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage(main.getPrefix() + "Ce joueur n'existe pas ou n'est pas connecté(e).");
                return;
            }

            searchPlayers(player, target, args);
            return;
        }

    }

    private void searchPlayers(Player player, Player target, String[] args) {
        int radius = 0;
        try {
            radius = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(main.getPrefix() + "Merci de mettre un nombre valide. La commande est : /near <radius> ou /near <radius> <joueur>");
            return;
        }

        List<String> playersName = new ArrayList<>();
        for (Entity entity : target.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player pl) {
                playersName.add(pl.getName());
            }
        }
            
        player.sendMessage(main.getPrefix() + "Les joueurs proches de vous sont : " + playersName);
    }
    
}