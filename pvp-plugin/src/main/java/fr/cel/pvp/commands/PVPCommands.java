package fr.cel.pvp.commands;

import fr.cel.gameapi.manager.command.AbstractCommand;
import fr.cel.pvp.arena.PVPArena;
import fr.cel.pvp.manager.GameManager;
import fr.cel.pvp.manager.PVPArenaManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    public void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (arenaManager.getArenas().isEmpty()) {
                sender.sendMessage(GameManager.getPrefix().append(Component.text("Aucune arène a été installée.")));
                return;
            }

            arenaManager.getArenas().values().forEach(arena -> 
                    sender.sendMessage(GameManager.getPrefix().append(Component.text("Arène " + arena.getDisplayName()))));
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(GameManager.getPrefix().append(Component.text("Les fichiers de configuration des arènes PVP ont été rechargées.")));
            gameManager.reloadArenaManager();
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(GameManager.getPrefix().append(Component.text("Vous devez etre un joueur pour effectuer cette commande.")));
            return;
        }

        if (!arenaManager.isPlayerInArena(player)) {
            player.sendMessage(GameManager.getPrefix().append(Component.text("Vous n'êtes pas dans une arène.")));
            return;
        }

        if (args[0].equalsIgnoreCase("listplayer")) {
            PVPArena arena = arenaManager.getArenaByPlayer(player);

            Component message = GameManager.getPrefix().append(Component.text("Joueurs : "));
            for (UUID pls : arena.getPlayers()) {
                Player pl = Bukkit.getPlayer(pls);
                if (pl != null) message = message.append(Component.text(pl.getName() + ", "));
            }

            player.sendMessage(message);
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
        sender.sendMessage(Component.text(" "));
        sender.sendMessage(Component.text("[Aide pour les commandes du PVP]", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/pvp reload : Recharge la configuration (les maps)"));
        sender.sendMessage(Component.text("/pvp list : Envoie la liste des maps"));
        sender.sendMessage(Component.text("/pvp listplayer : Envoie la liste des joueurs dans la carte où vous êtes"));
    }

}