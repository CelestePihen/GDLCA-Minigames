package fr.cel.cachecache.commands.subcommands;

import fr.cel.cachecache.manager.CCMapManager;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.state.game.PlayingMapState;
import fr.cel.gameapi.manager.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListSubCommand implements SubCommand {

    private final GameManager gameManager;
    private final CCMapManager mapManager;

    public ListSubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
        this.mapManager = gameManager.getMain().getCcMapManager();
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Envoie la liste des maps avec l'état du jeu actuel";
    }

    @Override
    public String getUsage() {
        return "/cc list [map]";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cachecache.admin");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (mapManager.getMaps().isEmpty()) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Aucune carte a été installée.", NamedTextColor.RED)));
            return;
        }

        if (args.length >= 1) {
            CCMap map = mapManager.getMaps().get(args[0].toLowerCase());
            if (map == null) {
                sender.sendMessage(gameManager.getPrefix().append(Component.text("Merci de mettre une carte valide.", NamedTextColor.RED)));
                return;
            }

            if (map.getMapState() instanceof PlayingMapState) {
                Player owner = Bukkit.getPlayer(map.getOwner());
                String ownerName = "...";
                if (owner != null) ownerName = owner.getName();

                sender.sendMessage(gameManager.getPrefix()
                        .append(Component.text("Carte " + map.getDisplayName(), NamedTextColor.YELLOW))
                        .appendNewline()
                        .append(Component.text("Temps : " + map.getTimer(), NamedTextColor.YELLOW))
                        .appendNewline()
                        .append(Component.text("Hôte : " + ownerName, NamedTextColor.YELLOW))
                        .appendNewline()
                        .append(Component.text("Nombre de joueurs : " + map.getPlayers().size(), NamedTextColor.YELLOW)));
            } else {
                sender.sendMessage(gameManager.getPrefix().append(Component.text("Aucune partie n'est en cours sur cette carte.", NamedTextColor.RED)));
            }
        } else {
            for (CCMap map : mapManager.getMaps().values()) {
                Component message = gameManager.getPrefix().append(Component.text("Carte " + map.getDisplayName() + " | " + map.getMapState().getName(), NamedTextColor.YELLOW));
                if (map.getMapState() instanceof PlayingMapState) {
                    message = message.append(Component.text(" | Timer : " + map.getTimer(), NamedTextColor.YELLOW));
                }
                sender.sendMessage(message);
            }
        }
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) return mapManager.getMaps().keySet().stream().toList();
        return List.of();
    }

}