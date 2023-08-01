package fr.cel.pvp.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.cel.pvp.manager.PVPGameManager;
import fr.cel.pvp.manager.arena.PVPArena;

public class PVPCommands implements CommandExecutor {

    private final PVPGameManager gameManager;

    public PVPCommands(PVPGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Vous devez etre un joueur pour faire cette commande.");
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("pvp.pvp")) {
            player.sendMessage(gameManager.getPrefix() + "Tu n'as pas la permission de faire cette commande.");
            return false;
        }

        else if (args[0].equalsIgnoreCase("list")) {
            if (gameManager.getArenaManager().getArenas().size() == 0) {
                player.sendMessage(gameManager.getPrefix() + "Aucune arène a été installée.");
                return false;
            }
            gameManager.getArenaManager().getArenas().forEach(arena -> { player.sendMessage(gameManager.getPrefix() + "Map " + arena.getDisplayName()); });
            return false;
        }

        else if (args[0].equalsIgnoreCase("listplayer")) {

            if (!gameManager.getArenaManager().isPlayerInArena(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une arène.");
                return false;
            }

            PVPArena arena = gameManager.getArenaManager().getArenaByPlayer(player);
            List<String> playersName = new ArrayList<>();
            for (UUID pls : arena.getPlayers()) {
                Player player1 = Bukkit.getPlayer(pls);
                playersName.add(player1.getName());
            }
            player.sendMessage(gameManager.getPrefix() + playersName);
            return false;
        }

        else if (args[0].equalsIgnoreCase("reload")) {
            player.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des arènes PVP ont été rechargées.");
            gameManager.reloadArenaManager();
        }

        return false;
    }

}