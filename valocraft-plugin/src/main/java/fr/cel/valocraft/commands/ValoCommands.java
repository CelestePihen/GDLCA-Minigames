package fr.cel.valocraft.commands;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.valocraft.manager.ValoGameManager;
import fr.cel.valocraft.manager.arena.ValoArena;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ValoCommands extends AbstractCommand {

    private final ValoGameManager gameManager;

    public ValoCommands(ValoGameManager gameManager) {
        super("valocraft:valocraft", false, true);
            this.gameManager = gameManager;
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des arènes ValoCraft ont été rechargées.");
            gameManager.reloadArenaManager();
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (gameManager.getArenaManager().getArenas().isEmpty()) {
                sender.sendMessage(gameManager.getPrefix() + "Aucune arène a été installée.");
                return;
            }
            gameManager.getArenaManager().getArenas().forEach(arena -> sender.sendMessage(gameManager.getPrefix() + "Map " + arena.getDisplayName() + " | " + arena.getArenaState().getClass().getSimpleName()));
            return;
        }

        if (!isPlayer(sender)) {
            sender.sendMessage(gameManager.getPrefix() + "Vous devez etre un joueur pour effectuer cette commande.");
            return;
        }

        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("start")) {
            ValoArena arena = gameManager.getArenaManager().getArenaByPlayer(player);

            if (arena == null) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une arène.");
                return;
            }

            if (arena.startGame()) {
                player.sendMessage(gameManager.getPrefix() + "La partie a été lancée !");
            } else {
                player.sendMessage(gameManager.getPrefix() + "La partie ne peut pas être lancée.");
            }
        }

        else if (args[0].equalsIgnoreCase("listplayer")) {
            ValoArena arena = gameManager.getArenaManager().getArenaByPlayer(player);

            if (arena == null) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une arène.");
                return;
            }

            List<String> players = getPlayerNames(arena.getPlayers());
            List<String> blueTeam = getPlayerNames(arena.getBlueTeam().getPlayers());
            List<String> redTeam = getPlayerNames(arena.getRedTeam().getPlayers());
            List<String> spectators = getPlayerNames(arena.getSpectators());

            player.sendMessage(gameManager.getPrefix() + "Joueurs :" + players + "\nTeam Bleu : " + blueTeam + "\nTeam Rouge : " + redTeam + " \nSpectateurs : " + spectators);
        }

        else if (args[0].equalsIgnoreCase("setround")) {
            ValoArena arena = gameManager.getArenaManager().getArenaByPlayer(player);

            if (arena == null) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une arène.");
                return;
            }

            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("blue")) {
                    arena.getBlueTeam().setRoundWin(Integer.parseInt(args[2]));
                } else if (args[1].equalsIgnoreCase("red")) {
                    arena.getRedTeam().setRoundWin(Integer.parseInt(args[2]));
                } else {
                    player.sendMessage(gameManager.getPrefix() + "La commande est : /valocraft setround <blue/red> <nombre>\n(sachant que si vous mettez un nombre supérieur ou égal à 13, cela finit la partie)");
                }
            } else {
                player.sendMessage(gameManager.getPrefix() + "La commande est : /valocraft setround <blue/red> <nombre>\n(sachant que si vous mettez un nombre supérieur ou égal à 13, cela finit la partie)");
            }

        }

    }

    private List<String> getPlayerNames(Set<UUID> playerUUIDs) {
        return playerUUIDs.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ChatUtility.format("[Aide pour les commandes du Valocraft]", ChatUtility.UtilityColor.GOLD));
        sender.sendMessage("/valo start : Commence la partie dans laquelle vous êtes");
        sender.sendMessage("/valo list : Envoie la liste des maps avec l'état du jeu actuel");
        sender.sendMessage("/valo listplayer : Envoie la liste des joueurs dans la partie où vous êtes");
        sender.sendMessage("/valo reload : Recharge la configuration (les maps)");
        sender.sendMessage("/valo setround <blue/red> <number> : Permet de changer le nombre de manches gagnées pour l'équipe choisie (sachant que si vous mettez un nombre supérieur ou égal à 13, cela finit la partie)");
    }

}