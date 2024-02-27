package fr.cel.pvp.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.cel.pvp.manager.PVPGameManager;
import fr.cel.pvp.manager.arena.PVPArena;

public class PVPCommands extends AbstractCommand {

    private final PVPGameManager gameManager;

    public PVPCommands(PVPGameManager gameManager) {
        super("pvp:pvp", true, true);
        this.gameManager = gameManager;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Vous devez etre un joueur pour faire cette commande.");
            return;
        }

        if (!player.hasPermission("pvp.pvp")) {
            player.sendMessage(gameManager.getPrefix() + "Tu n'as pas la permission de faire cette commande.");
        }

        else if (args[0].equalsIgnoreCase("list")) {
            if (gameManager.getArenaManager().getArenas().isEmpty()) {
                player.sendMessage(gameManager.getPrefix() + "Aucune arène a été installée.");
                return;
            }
            gameManager.getArenaManager().getArenas().forEach(arena -> { player.sendMessage(gameManager.getPrefix() + "Map " + arena.getDisplayName()); });
        }

        else if (args[0].equalsIgnoreCase("listplayer")) {

            if (!gameManager.getArenaManager().isPlayerInArena(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une arène.");
                return;
            }

            PVPArena arena = gameManager.getArenaManager().getArenaByPlayer(player);
            List<String> playersName = new ArrayList<>();
            for (UUID pls : arena.getPlayers()) {
                Player player1 = Bukkit.getPlayer(pls);
                if (player1 != null) playersName.add(player1.getName());
            }
            player.sendMessage(gameManager.getPrefix() + playersName);
        }

        else if (args[0].equalsIgnoreCase("reload")) {
            player.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des arènes PVP ont été rechargées.");
            gameManager.reloadArenaManager();
        }

    }

}