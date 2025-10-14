package fr.cel.cachecache.commands.subcommands;

import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ListPlayerSubCommand implements SubCommand {

    private final GameManager gameManager;

    public ListPlayerSubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "listplayer";
    }

    @Override
    public String getDescription() {
        return "Affiche la liste des joueurs sur une map";
    }

    @Override
    public String getUsage() {
        return "/cc listplayer <map>";
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
        if (args.length < 1) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Merci de sélectionner une carte.", NamedTextColor.RED)));
            return;
        }

        CCMap map = gameManager.getMain().getCcMapManager().getMaps().get(args[0]);
        if (map == null) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Carte invalide.", NamedTextColor.RED)));
            return;
        }

        if (map.getPlayers().isEmpty()) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Aucun joueur n'est sur la carte " + map.getDisplayName(), NamedTextColor.GREEN)));
            return;
        }

        Component message = gameManager.getPrefix().append(Component.text("Joueurs : "));

        for (UUID uuidPlayer : map.getPlayers()) {
            Player pl = Bukkit.getPlayer(uuidPlayer);
            if (pl != null && pl.isOnline()) message = message.append(Component.text(pl.getName() + ", "));
        }

        sender.sendMessage(message);
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) return gameManager.getMain().getCcMapManager().getMaps().keySet().stream().toList();
        return List.of();
    }
}