package fr.cel.pvp.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.gameapi.utils.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.cel.pvp.manager.GameManager;
import fr.cel.pvp.arena.PVPArena;

public class PVPCommands extends AbstractCommand {

    private final GameManager gameManager;

    public PVPCommands(GameManager gameManager) {
        super("pvp:pvp", false, true);
        this.gameManager = gameManager;
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (gameManager.getMain().getPvpArenaManager().getArenas().isEmpty()) {
                sender.sendMessage(gameManager.getPrefix() + "Aucune arène a été installée.");
                return;
            }
            gameManager.getMain().getPvpArenaManager().getArenas().forEach(arena -> sender.sendMessage(gameManager.getPrefix() + "Map " + arena.getDisplayName()));
        }

        else if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des arènes PVP ont été rechargées.");
            gameManager.reloadArenaManager();
            return;
        }

        if (!isPlayer(sender)) {
            sender.sendMessage(gameManager.getPrefix() + "Vous devez etre un joueur pour effectuer cette commande.");
            return;
        }

        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("listplayer")) {

            if (!gameManager.getMain().getPvpArenaManager().isPlayerInArena(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une arène.");
                return;
            }

            PVPArena arena = gameManager.getMain().getPvpArenaManager().getArenaByPlayer(player);
            List<String> playersName = new ArrayList<>();
            for (UUID pls : arena.getPlayers()) {
                Player player1 = Bukkit.getPlayer(pls);
                if (player1 != null) playersName.add(player1.getName());
            }
            player.sendMessage(gameManager.getPrefix() + playersName);
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("reload", "list", "listplayer");
        }

        return null;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ChatUtility.format("[Aide pour les commandes du PVP]", ChatUtility.GOLD));
        sender.sendMessage("/pvp reload : Recharge la configuration (les maps)");
        sender.sendMessage("/pvp list : Envoie la liste des maps");
        sender.sendMessage("/pvp listplayer : Envoie la liste des joueurs dans la carte où vous êtes");
    }

}