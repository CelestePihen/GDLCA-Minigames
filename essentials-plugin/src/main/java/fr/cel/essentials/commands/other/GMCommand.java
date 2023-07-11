package fr.cel.essentials.commands.other;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class GMCommand extends AbstractCommand {

    public GMCommand(Essentials main) {
        super(main, "gm");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        
        if (args.length <= 0 || args.length >= 3) {
            player.sendMessage(main.getPrefix() + "La commande est : /gm <mode> ou /gm <mode> <joueur>");
            return;
        }

        if (args.length == 1) {
            GameMode mode = getGamemode(args[0]);
            if (mode == null) {
                player.sendMessage(main.getPrefix() + "Merci de mettre un mode de jeu valide.");
                return;
            }
            String modeString = getModeName(mode);
            
            player.setGameMode(mode);
            player.sendMessage(main.getPrefix() + "Vous êtes en " + modeString + ".");
            return;
        }

        if (args.length == 2) {
            GameMode mode = getGamemode(args[0]);
            if (mode == null) {
                player.sendMessage(main.getPrefix() + "Merci de mettre un mode de jeu valide.");
                return;
            }
            String modeString = getModeName(mode);
            
            Player target = Bukkit.getPlayer(args[1]);

            if (target != null) {
                target.setGameMode(mode);
                target.sendMessage(main.getPrefix() + "Vous avez été mis(e) en " + modeString);
                player.sendMessage(main.getPrefix() + "Vous avez mis " + target.getName() + " en " + modeString + ".");
                return;
            } else {
                player.sendMessage(main.getPrefix() + "Ce joueur n'existe pas ou n'est pas connecté.");
                return;
            }
        }

    }

    private GameMode getGamemode(String mode) {
        switch (mode) {
            case "0": case "survival": case "survie": return GameMode.SURVIVAL;
            case "1": case "creative": case "creatif": return GameMode.CREATIVE;
            case "2": case "adventure": case "aventure": return GameMode.ADVENTURE;
            case "3": case "spectator": case "spectateur": return GameMode.SPECTATOR;
            default: return null;
        }
    }

    private String getModeName(GameMode gameMode) {
        if (gameMode.equals(GameMode.SURVIVAL)) return "Survie";
        else if (gameMode.equals(GameMode.CREATIVE)) return "Créatif";
        else if (gameMode.equals(GameMode.ADVENTURE)) return "Aventure";
        else if (gameMode.equals(GameMode.SPECTATOR)) return "Spectateur";
        return null;
    }
    
}
