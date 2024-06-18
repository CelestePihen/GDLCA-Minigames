package fr.cel.parkour.commands;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.parkour.manager.GameManager;
import fr.cel.parkour.map.ParkourMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParkourCommands extends AbstractCommand {

    private final GameManager gameManager;

    public ParkourCommands(GameManager gameManager) {
        super("parkour:parkour", false, true);
        this.gameManager = gameManager;
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (gameManager.getMain().getParkourMapManager().getMaps().isEmpty()) {
                sender.sendMessage(gameManager.getPrefix() + "Aucune map a été installee.");
            } else {
                gameManager.getMain().getParkourMapManager().getMaps().forEach((s, map) -> sendMessageWithPrefix(sender, "Parkour " + map.getDisplayName()));
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

            if (!gameManager.getMain().getParkourMapManager().isPlayerInArena(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une map Parkour.");
                return;
            }

            ParkourMap arena = gameManager.getMain().getParkourMapManager().getArenaByPlayer(player);
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
            return List.of("list", "listplayer", "reload");
        }
        return null;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ChatUtility.format("[Aide pour les commandes du Parkour]", ChatUtility.GOLD));
        sender.sendMessage("/parkour list : Envoie la liste des maps avec l'état du jeu actuel");
        sender.sendMessage("/parkour listplayer : Envoie la liste des joueurs dans la partie où vous êtes");
        sender.sendMessage("/parkour reload : Recharge les cartes");
    }

}