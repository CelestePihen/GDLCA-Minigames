package fr.cel.cachecache.commands.subcommands;

import fr.cel.cachecache.manager.CCMapManager;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.map.TemporaryHub;
import fr.cel.gameapi.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TempHubSubCommand implements SubCommand {

    private final GameManager gameManager;
    private final CCMapManager mapManager;

    public TempHubSubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
        this.mapManager = gameManager.getMain().getCcMapManager();
    }

    @Override
    public String getName() {
        return "starttemphub";
    }

    @Override
    public String getDescription() {
        return "Commence la partie du mode temporaire";
    }

    @Override
    public String getUsage() {
        return "/cc starttemphub";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("cachecache.admin");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return;

        TemporaryHub temporaryHub = mapManager.getTemporaryHub();
        if (!temporaryHub.isPlayerInTempHub(player)) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Vous n'êtes pas dans le Hub Temporaire.", NamedTextColor.RED)));
            return;
        }

        temporaryHub.chooseMapAndSendPlayers(player);
    }

}