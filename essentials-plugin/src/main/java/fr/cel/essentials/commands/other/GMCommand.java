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
        
        if (args.length == 0 || args.length >= 2) {
            sendMessageWithPrefix(player, "La commande est : /gm <mode> ou /gm <mode> <joueur>");
            return;
        }

        if (args.length == 1) {
            GameMode mode = getGamemode(args[0]);
            if (mode == null) {
                sendMessageWithPrefix(player, "Merci de mettre un mode de jeu valide.");
                return;
            }
            
            player.setGameMode(mode);
            sendMessageWithPrefix(player, "Vous êtes en " + getModeName(mode) + ".");
            return;
        }

        if (args.length == 2) {
            GameMode mode = getGamemode(args[0]);
            if (mode == null) {
                sendMessageWithPrefix(player, "Merci de mettre un mode de jeu valide.");
                return;
            }

            String modeString = getModeName(mode);
            Player target = Bukkit.getPlayer(args[1]);

            if (target != null) {
                target.setGameMode(mode);
                sendMessageWithPrefix(target, "Vous avez été mis(e) en " + modeString);
                sendMessageWithPrefix(player, "Vous avez mis " + target.getName() + " en " + modeString + ".");
            } else {
                sendMessageWithPrefix(player, "Ce joueur n'existe pas ou n'est pas connecté.");
            }
        }

    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}

    private GameMode getGamemode(String mode) {
        return switch (mode) {
            case "0", "survival", "survie" -> GameMode.SURVIVAL;
            case "1", "creative", "creatif" -> GameMode.CREATIVE;
            case "2", "adventure", "aventure" -> GameMode.ADVENTURE;
            case "3", "spectator", "spectateur" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    private String getModeName(GameMode gameMode) {
        return switch (gameMode) {
            case SURVIVAL -> "Survie";
            case CREATIVE -> "Créatif";
            case ADVENTURE -> "Aventure";
            case SPECTATOR -> "Spectateur";
        };
    }
    
}
