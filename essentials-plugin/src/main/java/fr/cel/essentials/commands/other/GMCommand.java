package fr.cel.essentials.commands.other;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GMCommand extends AbstractCommand {

    public GMCommand() {
        super("essentials:gm", false, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0 || args.length > 2) {
            sendMessageWithPrefix(sender, "La commande est : /gm <mode> ou /gm <mode> <joueur>");
            return;
        }

        if (args.length == 1 && sender instanceof Player player) {
            GameMode mode = getGamemode(args[0]);
            if (mode == null) {
                sendMessageWithPrefix(player, "Merci de mettre un mode de jeu valide.");
                return;
            }
            
            player.setGameMode(mode);
            sendMessageWithPrefix(player, "Vous êtes en " + getModeName(mode) + ".");
        }

        if (args.length == 2) {
            GameMode mode = getGamemode(args[0]);
            if (mode == null) {
                sendMessageWithPrefix(sender, "Merci de mettre un mode de jeu valide.");
                return;
            }

            String modeString = getModeName(mode);
            Player target = Bukkit.getPlayer(args[1]);

            if (isPlayerOnline(target, sender)) {
                target.setGameMode(mode);
                sendMessageWithPrefix(target, "Vous avez été mis(e) en " + modeString);
                sendMessageWithPrefix(sender, "Vous avez mis " + target.getName() + " en " + modeString + ".");
            }
        }

    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("0", "1", "2", "3", "s", "c", "a", "sp");
        }
        return null;
    }

    private GameMode getGamemode(String mode) {
        return switch (mode) {
            case "0", "s", "survival", "survie" -> GameMode.SURVIVAL;
            case "1", "c", "creative", "creatif" -> GameMode.CREATIVE;
            case "2", "a", "adventure", "aventure" -> GameMode.ADVENTURE;
            case "3", "sp", "spectator", "spectateur" -> GameMode.SPECTATOR;
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
