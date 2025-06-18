package fr.cel.valocraft.commands;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.pregame.PreGameArenaState;
import fr.cel.valocraft.manager.GameManager;
import fr.cel.valocraft.manager.ValoArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ValoCommands extends AbstractCommand {

    private final GameManager gameManager;
    private final ValoArenaManager arenaManager;

    public ValoCommands(GameManager gameManager) {
        super("valocraft:valocraft", false, true);
        this.gameManager = gameManager;
        this.arenaManager = gameManager.getValoArenaManager();
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sendMessageWithPrefix(sender, "Les fichiers de configuration des arènes ValoCraft ont été rechargées.");
            gameManager.reloadArenaManager();
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (arenaManager.getArenas().isEmpty()) {
                sendMessageWithPrefix(sender, "Aucune arène a été installée.");
                return;
            }
            arenaManager.getArenas().forEach(arena ->
                    sender.sendMessage(gameManager.getPrefix() + "Map " + arena.getDisplayName() + " | " + arena.getArenaState().getClass().getSimpleName())
            );
            return;
        }

        if (!(sender instanceof Player player)) {
            sendMessageWithPrefix(sender, "Vous devez etre un joueur pour effectuer cette commande.");
            return;
        }

        if (!arenaManager.isPlayerInArena(player)) {
            sendMessageWithPrefix(player, "Vous n'êtes pas dans une carte.");
            return;
        }

        final ValoArena arena = arenaManager.getArenaByPlayer(player);

        if (args[0].equalsIgnoreCase("start")) {
            if (arena == null) {
                sendMessageWithPrefix(player, "Vous n'êtes pas dans une arène.");
                return;
            }

            if (arena.startGame()) {
                sendMessageWithPrefix(player, "La partie a été lancée !");
            } else {
                sendMessageWithPrefix(player, "La partie ne peut pas être lancée.");
            }
        }

        else if (args[0].equalsIgnoreCase("listplayer")) {
            List<String> players = getPlayerNames(arena.getPlayers());
            List<String> blueTeam = getPlayerNames(arena.getBlueTeam().getPlayers());
            List<String> redTeam = getPlayerNames(arena.getRedTeam().getPlayers());
            List<String> spectators = getPlayerNames(arena.getSpectators());

            sendMessageWithPrefix(player,
                    "Joueurs :" + players +
                    "\nTeam Bleu : " + blueTeam +
                    "\nTeam Rouge : " + redTeam +
                    " \nSpectateurs : " + spectators
            );
        }

        else if (args[0].equalsIgnoreCase("setround")) {
            if (arena.getArenaState() instanceof PreGameArenaState) {
                sendMessageWithPrefix(player, "La partie n'est pas lancée.");
                return;
            }

            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("blue")) {
                    arena.getBlueTeam().setRoundWin(Integer.parseInt(args[2]));
                } else if (args[1].equalsIgnoreCase("red")) {
                    arena.getRedTeam().setRoundWin(Integer.parseInt(args[2]));
                } else {
                    sendMessageWithPrefix(player, "La commande est : /valocraft setround <blue/red> <nombre>\n(sachant que si vous mettez un nombre supérieur ou égal à 13, cela finit la partie)");
                }
            } else {
                sendMessageWithPrefix(player, "La commande est : /valocraft setround <blue/red> <nombre>\n(sachant que si vous mettez un nombre supérieur ou égal à 13, cela finit la partie)");
            }

        }

    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("start", "list", "listplayer", "reload", "setround");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("setround")) {
            return List.of("blue", "red");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("setround")) {
            return IntStream.range(1, 10).mapToObj(Integer::toString).collect(Collectors.toList());
        }

        return null;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ChatUtility.format("[Aide pour les commandes du Valocraft]", ChatUtility.GOLD));
        sender.sendMessage("/valo start : Commence la partie dans laquelle vous êtes");
        sender.sendMessage("/valo list : Envoie la liste des maps avec l'état du jeu actuel");
        sender.sendMessage("/valo listplayer : Envoie la liste des joueurs dans la partie où vous êtes");
        sender.sendMessage("/valo reload : Recharge la configuration (les maps)");
        sender.sendMessage("/valo setround <blue/red> <number> : Permet de changer le nombre de manches gagnées pour l'équipe choisie (sachant que si vous mettez un nombre supérieur ou égal à 13, cela finit la partie)");
    }

    private List<String> getPlayerNames(List<UUID> playerUUIDs) {
        return playerUUIDs.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).map(Player::getName).collect(Collectors.toList());
    }

    private List<String> getPlayerNames(Set<UUID> playerUUIDs) {
        return playerUUIDs.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).map(Player::getName).collect(Collectors.toList());
    }

}