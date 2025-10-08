package fr.cel.essentials.commands.general;

import fr.cel.gameapi.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FlyCommand extends AbstractCommand {

    public FlyCommand() {
        super("essentials:fly", false, true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {

        if (sender instanceof Player player && args.length == 0) {
            if (player.getGameMode() == GameMode.SPECTATOR) {
                sendMessageWithPrefix(player, Component.text("Vous ne pouvez pas faire cette commande en spectateur."));
                return;
            }

            toggleFly(player, player);
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (!isPlayerOnline(target, sender)) return;

            if (target.getGameMode() == GameMode.SPECTATOR) {
                sendMessageWithPrefix(sender, Component.text("Vous ne pouvez pas modifier le vol d'un joueur qui est en spectateur."));
                return;
            }

            toggleFly(target, sender);
            return;
        }

        sendMessageWithPrefix(sender, Component.text("Usage: /fly [player]"));
    }

    /**
     * Toggles fly for a player and sends appropriate feedback messages.
     *
     * @param target The player whose fly state is toggled
     * @param sender The command sender
     */
    private void toggleFly(@NotNull Player target, @NotNull CommandSender sender) {
        target.setAllowFlight(!target.getAllowFlight());
        boolean enabled = target.getAllowFlight();

        if (enabled) {
            sendMessageWithPrefix(sender, Component.text("Tu as le fly."));
            if (sender != target)
                sendMessageWithPrefix(sender, Component.text("Tu as donné le fly à " + target.getName() + "."));
        } else {
            sendMessageWithPrefix(sender, Component.text("Tu n'as plus le fly."));
            if (sender != target)
                sendMessageWithPrefix(sender, Component.text("Tu as enlevé le fly à " + target.getName() + "."));
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return List.of();
    }
}
