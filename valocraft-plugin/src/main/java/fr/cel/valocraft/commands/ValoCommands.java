package fr.cel.valocraft.commands;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.cel.valocraft.manager.ValoGameManager;
import fr.cel.valocraft.manager.arena.ValoArena;

public class ValoCommands implements CommandExecutor {

    private final ValoGameManager gameManager;

    public ValoCommands(ValoGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(gameManager.getPrefix() + "Vous devez mettre : /valocraft <start/list/listplayer/reload>");
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des arènes ValoCraft ont été rechargées.");
            gameManager.reloadArenaManager();
            return false;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (gameManager.getArenaManager().getArenas().isEmpty()) {
                sender.sendMessage(gameManager.getPrefix() + "Aucune arène a été installée.");
                return false;
            }
            gameManager.getArenaManager().getArenas().forEach(arena -> sender.sendMessage(gameManager.getPrefix() + "Map " + arena.getDisplayName() + " | " + arena.getArenaState().getClass().getSimpleName()));
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Vous devez etre un joueur pour faire cette commande.");
            return false;
        }

        if (args[0].equalsIgnoreCase("start")) {
            ValoArena arena = gameManager.getArenaManager().getArenaByPlayer(player);

            if (arena == null) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une arène.");
                return false;
            }

            if (arena.startGame()) {
                player.sendMessage(gameManager.getPrefix() + "La partie a été lancée !");
            } else {
                player.sendMessage(gameManager.getPrefix() + "La partie ne peut pas être lancée.");
            }
            return false;
        }

        else if (args[0].equalsIgnoreCase("listplayer")) {
            ValoArena arena = gameManager.getArenaManager().getArenaByPlayer(player);

            if (arena == null) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une arène.");
                return false;
            }

            List<String> players = getPlayerNames(arena.getPlayers());
            List<String> blueTeam = getPlayerNames(arena.getBlueTeam().getPlayers());
            List<String> redTeam = getPlayerNames(arena.getRedTeam().getPlayers());

            player.sendMessage(gameManager.getPrefix() + "Joueurs :" + players + "\nTeam Bleu : " + blueTeam + "\nTeam Rouge : " + redTeam);
            return false;
        }

        else if (args[0].equalsIgnoreCase("setround")) {
            ValoArena arena = gameManager.getArenaManager().getArenaByPlayer(player);

            if (arena == null) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une arène.");
                return false;
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
                return false;
            }

        }

        return false;
    }

    private List<String> getPlayerNames(List<UUID> playerUUIDs) {
        return playerUUIDs.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .map(Player::getName)
                .collect(Collectors.toList());
    }

}