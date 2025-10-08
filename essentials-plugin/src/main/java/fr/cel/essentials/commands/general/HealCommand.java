package fr.cel.essentials.commands.general;

import fr.cel.gameapi.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class HealCommand extends AbstractCommand {

    public HealCommand() {
        super("essentials:heal", false, true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sendMessageWithPrefix(sender, Component.text("Usage: /heal <player>"));
                return;
            }

            healPlayer(player, sender);
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (!isPlayerOnline(target, sender)) return;

            healPlayer(target, sender);
            return;
        }

        sendMessageWithPrefix(sender, Component.text("Usage: /heal [player]"));
    }

    /**
     * Heal the player at his max health.
     *
     * @param target The player to heal
     * @param sender The sender to the command
     */
    private void healPlayer(@NotNull Player target, @NotNull CommandSender sender) {
        double maxHealth = Objects.requireNonNull(target.getAttribute(Attribute.MAX_HEALTH)).getValue();
        target.setHealth(maxHealth);
        target.setFireTicks(0);
        target.setFoodLevel(20);
        target.setSaturation(20f);

        sendMessageWithPrefix(sender, Component.text("Vous avez été soigné(e)."));
        if (sender != target) {
            sendMessageWithPrefix(sender, Component.text("Vous avez soigné " + target.getName() + "."));
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return List.of();
    }
}