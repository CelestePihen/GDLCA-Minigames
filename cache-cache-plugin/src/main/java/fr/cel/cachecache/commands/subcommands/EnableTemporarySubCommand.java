package fr.cel.cachecache.commands.subcommands;

import fr.cel.cachecache.manager.CCMapManager;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.map.TemporaryHub;
import fr.cel.gameapi.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EnableTemporarySubCommand implements SubCommand {

    private final GameManager gameManager;
    private final CCMapManager mapManager;

    public EnableTemporarySubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
        this.mapManager = gameManager.getMain().getCcMapManager();
    }

    @Override
    public String getName() {
        return "enabletemporary";
    }

    @Override
    public String getDescription() {
        return "Active ou désactive le mode temporaire";
    }

    @Override
    public String getUsage() {
        return "/cc enableTemporary";
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
        TemporaryHub temporaryHub = mapManager.getTemporaryHub();
        if (temporaryHub.hasNoMaps()) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Aucune carte pour le mode de jeu temporaire du Cache-Cache a été installée.", NamedTextColor.RED)));
            return;
        }

        temporaryHub.setActivated(!temporaryHub.isActivated());
        if (temporaryHub.isActivated()) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Le mode de jeu temporaire du Cache-Cache est activé !", NamedTextColor.GREEN)));
        } else {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Le mode de jeu temporaire du Cache-Cache est désactivé !", NamedTextColor.RED)));
        }
    }

}