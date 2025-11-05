package fr.cel.cachecache.commands.subcommands;

import fr.cel.cachecache.manager.GameManager;
import fr.cel.gameapi.manager.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadSubCommand implements SubCommand {

    private final GameManager gameManager;

    public ReloadSubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Recharge les maps";
    }

    @Override
    public String getUsage() {
        return "/cc reload";
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
        gameManager.reloadMapManager();
        sender.sendMessage(gameManager.getPrefix().append(Component.text("Les fichiers de configuration des maps Cache-Cache ont été rechargés.", NamedTextColor.GREEN)));
    }
}
