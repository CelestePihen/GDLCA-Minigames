package fr.cel.cachecache.commands;

import fr.cel.cachecache.manager.CCGameManager;
import fr.cel.cachecache.manager.arena.CCArena;
import fr.cel.cachecache.manager.arena.TemporaryHub;
import fr.cel.cachecache.manager.arena.state.pregame.PreGameArenaState;
import fr.cel.cachecache.manager.arena.state.pregame.StartingArenaState;
import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.gameapi.utils.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CCCommand extends AbstractCommand {

    private final CCGameManager gameManager;

    public CCCommand(CCGameManager gameManager) {
        super("cachecache:cachecache", false, true);
        this.gameManager = gameManager;
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des maps Cache-Cache ont été rechargés.");
            gameManager.reloadArenaManager();
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (gameManager.getArenaManager().getArenas().isEmpty()) {
                sender.sendMessage(gameManager.getPrefix() + "Aucune carte a été installee.");
                return;
            }
            for (CCArena arena : gameManager.getArenaManager().getArenas().values()) {
                sender.sendMessage(gameManager.getPrefix() + "Carte " + arena.getDisplayName() + " | " + arena.getArenaState().getClass().getSimpleName());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("enableTemporary")) {
            final TemporaryHub temporaryHub = gameManager.getArenaManager().getTemporaryHub();
            temporaryHub.setActivated(!temporaryHub.isActivated());

            if (temporaryHub.isActivated()) {
                sender.sendMessage(gameManager.getPrefix() + "Le mode de jeu temporaire du Cache-Cache est active !");
            } else {
                sender.sendMessage(gameManager.getPrefix() + "Le mode de jeu temporaire du Cache-Cache est désactive !");
            }
            return;
        }

        if (!isPlayer(sender)) {
            sender.sendMessage(gameManager.getPrefix() + "Vous devez etre un joueur pour effectuer cette commande.");
            return;
        }

        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("start")) {

            if (!gameManager.getArenaManager().isPlayerInArena(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une carte.");
                return;
            }

            final CCArena arena = gameManager.getArenaManager().getArenaByPlayer(player);

            if (arena.getArenaState() instanceof PreGameArenaState) {
                if (arena.getHunterMode() == CCArena.HunterMode.TwoHuntersAtStart) {
                    if (arena.getPlayers().size() <= 2) {
                        player.sendMessage(gameManager.getPrefix() + "Il n'y a pas assez de joueurs (minimum 3 joueurs) !");
                    } else {
                        arena.setArenaState(new StartingArenaState(arena));
                    }
                }

                else {
                    if (arena.getPlayers().size() < 2) {
                        player.sendMessage(gameManager.getPrefix() + "Il n'y a pas assez de joueurs (minimum 2 joueurs) !");
                    } else {
                        arena.setArenaState(new StartingArenaState(arena));
                    }
                }

            } else {
                player.sendMessage(gameManager.getPrefix() + "La partie est déjà lancée.");
            }
            return;
        }

        if (args[0].equalsIgnoreCase("tempHub")) {
            final TemporaryHub temporaryHub = gameManager.getArenaManager().getTemporaryHub();

            if (!temporaryHub.isPlayerInTempHub(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans le Hub Temporaire.");
                return;
            }

            temporaryHub.chooseMapAndSendPlayers(player);
            return;
        }

        if (args[0].equalsIgnoreCase("listplayer")) {

            if (!gameManager.getArenaManager().isPlayerInArena(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une carte.");
                return;
            }

            CCArena arena = gameManager.getArenaManager().getArenaByPlayer(player);
            List<String> playersName = new ArrayList<>();
            arena.getPlayers().forEach(pls -> playersName.add(Bukkit.getPlayer(pls).getName()));
            player.sendMessage(gameManager.getPrefix() + "Joueurs: " + playersName);
        }

    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ChatUtility.format("[Aide pour les commandes du Cache-Cache]", ChatUtility.UtilityColor.GOLD));
        sender.sendMessage("/cc start : Commence la partie dans laquelle vous êtes");
        sender.sendMessage("/cc temphub : Commence la partie du mode temporaire");
        sender.sendMessage("/cc enableTemporary : Active/Désactive le mode temporaire");
        sender.sendMessage("/cc list : Envoie la liste des maps avec l'état du jeu actuel");
        sender.sendMessage("/cc listplayer : Envoie la liste des joueurs dans la partie où vous êtes");
        sender.sendMessage("/cc reload : Recharge la configuration (les maps)");
    }

}