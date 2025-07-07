package fr.cel.pvp.commands;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.pvp.arena.PVPArena;
import fr.cel.pvp.manager.GameManager;
import fr.cel.pvp.manager.PVPArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PVPCommands extends AbstractCommand {

    private final GameManager gameManager;
    private final PVPArenaManager arenaManager;

    public PVPCommands(GameManager gameManager) {
        super("pvp:pvp", false, true);
        this.gameManager = gameManager;
        this.arenaManager = gameManager.getMain().getPvpArenaManager();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (arenaManager.getArenas().isEmpty()) {
                sender.sendMessage(gameManager.getPrefix() + "Aucune arène a été installée.");
                return;
            }

            arenaManager.getArenas().values().forEach(arena -> 
                    sender.sendMessage(gameManager.getPrefix() + "Arène " + arena.getDisplayName()));
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des arènes PVP ont été rechargées.");
            gameManager.reloadArenaManager();
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(gameManager.getPrefix() + "Vous devez etre un joueur pour effectuer cette commande.");
            return;
        }

        if (!arenaManager.isPlayerInArena(player)) {
            player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une arène.");
            return;
        }

        if (args[0].equalsIgnoreCase("listplayer")) {
            PVPArena arena = arenaManager.getArenaByPlayer(player);

            StringBuilder sb = new StringBuilder();
            for (UUID pls : arena.getPlayers()) {
                Player pl = Bukkit.getPlayer(pls);
                if (pl != null) sb.append(pl.getName()).append(", ");
            }

            player.sendMessage(gameManager.getPrefix() + "Joueurs : " + sb);
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