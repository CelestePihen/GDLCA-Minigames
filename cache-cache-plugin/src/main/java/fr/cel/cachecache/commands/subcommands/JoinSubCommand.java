package fr.cel.cachecache.commands.subcommands;

import fr.cel.cachecache.manager.CCMapManager;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.map.CCMap;
import fr.cel.gameapi.manager.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JoinSubCommand implements SubCommand {

    private final GameManager gameManager;
    private final CCMapManager mapManager;

    public JoinSubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
        this.mapManager = gameManager.getMain().getCcMapManager();
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Permet à un joueur de rejoindre une map";
    }

    @Override
    public String getUsage() {
        return "/cc join <map> [player]";
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
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Merci de sélectionner une carte valide.", NamedTextColor.RED)));
            return;
        }

        CCMap map = mapManager.getMaps().get(args[0]);
        if (map == null) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Merci de sélectionner une carte valide.", NamedTextColor.RED)));
            return;
        }

        if (args.length == 2) {
            Player target = Bukkit.getPlayerExact(args[1]);
            if (isPlayerOnline(target, sender)) map.addPlayer(target, false);
        } else if (sender instanceof Player player && args.length == 1) {
            map.addPlayer(player, false);
        } else {
            sender.sendMessage(Component.text("Usage: /cc join <map> <player>", NamedTextColor.RED));
        }
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) return mapManager.getMaps().keySet().stream().toList();
        return List.of();
    }

}