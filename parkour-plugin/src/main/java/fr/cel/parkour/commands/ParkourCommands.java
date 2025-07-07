package fr.cel.parkour.commands;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.parkour.manager.GameManager;
import fr.cel.parkour.map.ParkourMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            gameManager.reloadMapManager();
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(gameManager.getPrefix() + "Vous devez etre un joueur pour effectuer cette commande.");
            return;
        }

        if (!gameManager.getMain().getParkourMapManager().isPlayerInMap(player)) {
            player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une map Parkour.");
            return;
        }

        if (args[0].equalsIgnoreCase("listplayer")) {
            ParkourMap map = gameManager.getMain().getParkourMapManager().getMapByPlayer(player);

            StringBuilder sb = new StringBuilder();
            for (UUID pls : map.getPlayers()) {
                Player pl = Bukkit.getPlayer(pls);
                if (pl != null) sb.append(pl.getName()).append(", ");
            }

            player.sendMessage(gameManager.getPrefix() + "Joueurs : " + sb);
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