package fr.cel.cachecache.commands;

import java.util.ArrayList;
import java.util.List;

import fr.cel.cachecache.manager.arena.TemporaryHub;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.CCGameManager;
import fr.cel.cachecache.manager.arena.state.pregame.PreGameArenaState;
import fr.cel.cachecache.manager.arena.state.pregame.StartingArenaState;

public class CCCommands implements CommandExecutor {

    private final CCGameManager gameManager;

    public CCCommands(CCGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des maps Cache-Cache ont été rechargés.");
            gameManager.reloadArenaManager();
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (gameManager.getArenaManager().getArenas().isEmpty()) {
                sender.sendMessage(gameManager.getPrefix() + "Aucune carte a été installee.");
                return false;
            }
            for (CCArena arena : gameManager.getArenaManager().getArenas().values()) {
                sender.sendMessage(gameManager.getPrefix() + "Carte " + arena.getDisplayName() + " | " + arena.getArenaState());
            }
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Vous devez etre un joueur pour faire cette commande.");
            return false;
        }

        if (!player.hasPermission("cachecache.cachecommand")) {
            player.sendMessage(gameManager.getPrefix() + "Tu n'as pas la permission de faire cette commande.");
            return false;
        }

        if (args[0].equalsIgnoreCase("start")) {

            if (!gameManager.getArenaManager().isPlayerInArena(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une carte.");
                return false;
            }

            final CCArena arena = gameManager.getArenaManager().getArenaByPlayer(player);

            if (arena.getArenaState() instanceof PreGameArenaState) {
                if (arena.getHunterMode() == CCArena.HunterMode.TwoHuntersAtStart) {
                    if (arena.getPlayers().size() <= 2) {
                        player.sendMessage(gameManager.getPrefix() + "Il n'y a pas assez de joueurs (minimum 3 joueurs) !");
                        return false;
                    } else {
                        arena.setArenaState(new StartingArenaState(arena));
                        return true;
                    }
                }

                else {
                    if (arena.getPlayers().size() <= 2) {
                        player.sendMessage(gameManager.getPrefix() + "Il n'y a pas assez de joueurs (minimum 2 joueurs) !");
                        return false;
                    } else {
                        arena.setArenaState(new StartingArenaState(arena));
                        return true;
                    }
                }

            } else {
                player.sendMessage(gameManager.getPrefix() + "La partie est déjà lancée.");
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("enableTemporary")) {
            final TemporaryHub temporaryHub = gameManager.getArenaManager().getTemporaryHub();
            temporaryHub.setActivated(!temporaryHub.isActivated());

            if (temporaryHub.isActivated()) {
                player.sendMessage(gameManager.getPrefix() + "Le mode de jeu temporaire du Cache-Cache est activé !");
            } else {
                player.sendMessage(gameManager.getPrefix() + "Le mode de jeu temporaire du Cache-Cache est désactivé !");
            }
        }

        if (args[0].equalsIgnoreCase("tempHub")) {
            final TemporaryHub temporaryHub = gameManager.getArenaManager().getTemporaryHub();

            if (!temporaryHub.isPlayerInTempHub(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans le Hub Temporaire.");
                return false;
            }

            temporaryHub.chooseMapAndSendPlayers(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("listplayer")) {

            if (!gameManager.getArenaManager().isPlayerInArena(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une carte.");
                return false;
            }

            CCArena arena = gameManager.getArenaManager().getArenaByPlayer(player);
            List<String> playersName = new ArrayList<>();
            arena.getPlayers().forEach(pls -> playersName.add(Bukkit.getPlayer(pls).getName()));
            player.sendMessage(gameManager.getPrefix() + "Joueurs: " + playersName);
            return true;
        }

        return false;
    }

}