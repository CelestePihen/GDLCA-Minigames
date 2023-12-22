package fr.cel.parkour.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.cel.parkour.manager.ParkourGameManager;
import fr.cel.parkour.manager.area.ParkourMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ParkourCommands implements CommandExecutor {

    private final ParkourGameManager gameManager;

    public ParkourCommands(ParkourGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Vous devez etre un joueur pour faire cette commande.");
            return false;
        }

        if (!player.hasPermission("parkour.parkour")) {
            player.sendMessage(gameManager.getPrefix() + "Tu n'as pas la permission de faire cette commande.");
            return false;
        }

        else if (args[0].equalsIgnoreCase("list")) {
            if (gameManager.getMapManager().getArenas().isEmpty()) {
                player.sendMessage(gameManager.getPrefix() + "Aucune map a été installée.");
                return false;
            }
            gameManager.getMapManager().getArenas().forEach(arena -> player.sendMessage(gameManager.getPrefix() + "Parkour " + arena.getDisplayName()));
            return false;
        }

        else if (args[0].equalsIgnoreCase("listplayer")) {

            if (!gameManager.getMapManager().isPlayerInArena(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une map Parkour.");
                return false;
            }

            ParkourMap arena = gameManager.getMapManager().getArenaByPlayer(player);
            List<String> playersName = new ArrayList<>();
            for (UUID pls : arena.getPlayers()) {
                Player player1 = Bukkit.getPlayer(pls);
                if (player1 != null) playersName.add(player1.getName());
            }
            player.sendMessage(gameManager.getPrefix() + playersName);
            return false;
        }

        else if (args[0].equalsIgnoreCase("reload")) {
            player.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des maps Parkour ont été rechargées.");
            gameManager.reloadArenaManager();
        }

        return false;
    }

}