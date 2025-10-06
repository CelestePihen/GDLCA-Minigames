package fr.cel.parkour.commands;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.parkour.manager.GameManager;
import fr.cel.parkour.map.ParkourMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ParkourCommands extends AbstractCommand {

    private final GameManager gameManager;

    public ParkourCommands(GameManager gameManager) {
        super("parkour:parkour", false, true);
        this.gameManager = gameManager;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (gameManager.getMain().getParkourMapManager().getMaps().isEmpty()) {
                sender.sendMessage(GameManager.getPrefix().append(Component.text("Aucune map a été installee.")));
                return;
            }

            gameManager.getMain().getParkourMapManager().getMaps().forEach((s, map) ->
                    sender.sendMessage(GameManager.getPrefix().append(Component.text("Parkour " + map.getDisplayName()))));
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(GameManager.getPrefix().append(Component.text("Les fichiers de configuration des maps Parkour ont été rechargees.")));
            gameManager.reloadMapManager();
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(GameManager.getPrefix().append(Component.text("Vous devez etre un joueur pour effectuer cette commande.")));
            return;
        }

        if (!gameManager.getMain().getParkourMapManager().isPlayerInMap(player)) {
            player.sendMessage(GameManager.getPrefix().append(Component.text("Vous n'êtes pas dans une map Parkour.")));
            return;
        }

        if (args[0].equalsIgnoreCase("listplayer")) {
            ParkourMap map = gameManager.getMain().getParkourMapManager().getMapByPlayer(player);

            Component message = GameManager.getPrefix().append(Component.text("Joueurs : "));
            for (UUID pls : map.getPlayers()) {
                Player pl = Bukkit.getPlayer(pls);
                if (pl != null) message = message.append(Component.text(pl.getName() + ", "));
            }

            player.sendMessage(message);
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
        sender.sendMessage(Component.text(" "));
        sender.sendMessage(Component.text("[Aide pour les commandes du Parkour]", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/parkour list : Envoie la liste des maps avec l'état du jeu actuel"));
        sender.sendMessage(Component.text("/parkour listplayer : Envoie la liste des joueurs dans la partie où vous êtes"));
        sender.sendMessage(Component.text("/parkour reload : Recharge les cartes"));
    }

}