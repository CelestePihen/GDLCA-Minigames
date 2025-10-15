package fr.cel.cachecache.commands.subcommands;

import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StartSubCommand implements SubCommand {

    private final GameManager gameManager;

    public StartSubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Force le démarrage d'une partie";
    }

    @Override
    public String getUsage() {
        return "/cc start <map>";
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

        map.startGame(sender);
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) return gameManager.getMain().getCcMapManager().getMaps().keySet().stream().toList();
        return List.of();
    }
}
