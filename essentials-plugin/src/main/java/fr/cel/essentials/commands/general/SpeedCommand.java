package fr.cel.essentials.commands.general;

import fr.cel.gameapi.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpeedCommand extends AbstractCommand {

    public SpeedCommand() {
        super("essentials:speed", false, true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0 || args.length >= 3) {
            sendMessageWithPrefix(sender, Component.text("Usage: /speed <value> [player]"));
            return;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sendMessageWithPrefix(sender, Component.text("Usage: /speed <value> <player>"));
                return;
            }

            Float speed = parseSpeed(args[0], sender);
            if (speed == null) return;

            applySpeed(player, speed);
            sendMessageWithPrefix(player, Component.text("Votre vitesse a été mise à " + (int) (speed * 10) + "."));
            return;
        }

        Float speed = parseSpeed(args[0], sender);
        if (speed == null) return;

        Player target = Bukkit.getPlayerExact(args[1]);
        if (!isPlayerOnline(target, sender)) return;

        applySpeed(target, speed);

        sendMessageWithPrefix(sender, Component.text("Vous avez mis la vitesse de " + target.getName() + " à " + (int) (speed * 10) + "."));
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return IntStream.range(1, 10).mapToObj(Integer::toString).collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Parses and validates the given speed argument.
     * @param arg the input string
     * @param sender the command sender (for feedback messages)
     * @return the parsed speed as a float (0.1f–1.0f), or null if invalid
     */
    private Float parseSpeed(@NotNull String arg, @NotNull CommandSender sender) {
        try {
            int value = Integer.parseInt(arg);
            if (value < 1 || value > 10) {
                sendMessageWithPrefix(sender, Component.text("Please enter a number between 1 and 10. Default speed: 2"));
                return null;
            }
            return value / 10f;
        } catch (NumberFormatException e) {
            sendMessageWithPrefix(sender, Component.text("Please enter a valid number between 1 and 10. Default speed: 2"));
            return null;
        }
    }

    /**
     * Applies the given speed to the player (fly or walk depending on current state).
     * @param player the target player
     * @param speed the new speed (0.1f–1.0f)
     */
    private void applySpeed(@NotNull Player player, float speed) {
        if (player.isFlying()) player.setFlySpeed(speed);
        else player.setWalkSpeed(speed);
    }

}