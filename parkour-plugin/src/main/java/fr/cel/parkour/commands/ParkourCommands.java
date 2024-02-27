package fr.cel.parkour.commands;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.parkour.manager.ParkourGameManager;
import fr.cel.parkour.manager.area.ParkourMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParkourCommands extends AbstractCommand {

    private final ParkourGameManager gameManager;

    public ParkourCommands(ParkourGameManager gameManager) {
        super("parkour:parkour", false, true);
        this.gameManager = gameManager;
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {

        if (args[0].equalsIgnoreCase("list")) {
            if (gameManager.getMapManager().getArenas().isEmpty()) {
                sender.sendMessage(gameManager.getPrefix() + "Aucune map a été installee.");
            } else {
                gameManager.getMapManager().getArenas().forEach(arena -> sender.sendMessage(gameManager.getPrefix() + "Parkour " + arena.getDisplayName()));
            }
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des maps Parkour ont été rechargees.");
            gameManager.reloadArenaManager();
        }

        if (!isPlayer(sender)) {
            sender.sendMessage(gameManager.getPrefix() + "Vous devez etre un joueur pour effectuer cette commande.");
            return;
        }

        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("listplayer")) {

            if (!gameManager.getMapManager().isPlayerInArena(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une map Parkour.");
                return;
            }

            ParkourMap arena = gameManager.getMapManager().getArenaByPlayer(player);
            List<String> playersName = new ArrayList<>();
            for (UUID pls : arena.getPlayers()) {
                Player player1 = Bukkit.getPlayer(pls);
                if (player1 != null) playersName.add(player1.getName());
            }
            player.sendMessage(gameManager.getPrefix() + playersName);
        }
    }

}