package fr.cel.cachecache.commands.subcommands;

import fr.cel.cachecache.manager.GameManager;
import fr.cel.gameapi.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadTemporarySubCommand implements SubCommand {

    private final GameManager gameManager;

    public ReloadTemporarySubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "reloadtemporary";
    }

    @Override
    public String getDescription() {
        return "Recharge les maps temporaires Cache-Cache";
    }

    @Override
    public String getUsage() {
        return "/cc reloadTemporary";
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
        gameManager.reloadTemporaryHub();
        sender.sendMessage(gameManager.getPrefix().append(Component.text("Les fichiers de configuration des maps temporaires Cache-Cache ont été rechargés.", NamedTextColor.GREEN)));
    }

}