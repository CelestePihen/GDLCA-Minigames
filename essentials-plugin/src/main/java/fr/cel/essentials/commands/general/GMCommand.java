package fr.cel.essentials.commands.general;

import fr.cel.gameapi.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GMCommand extends AbstractCommand {

    public GMCommand() {
        super("essentials:gm", false, true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0 || args.length > 2) {
            sendMessageWithPrefix(sender, Component.text("Usage: /gm <mode> [player]"));
            return;
        }

        GameMode mode = getGamemode(args[0]);
        if (mode == null) {
            sendMessageWithPrefix(sender, Component.text("Merci de mettre un mode de jeu valide."));
            return;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sendMessageWithPrefix(sender, Component.text("Usage: /gm <mode> <player>"));
                return;
            }

            player.setGameMode(mode);
            sendMessageWithPrefix(player, Component.text("Vous êtes en ").append(getModeName(mode)));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (!isPlayerOnline(target, sender)) return;

        target.setGameMode(mode);
        sendMessageWithPrefix(sender, Component.text("Vous avez mis ")
                .append(Component.text(target.getName()))
                .append(Component.text(" en "))
                .append(getModeName(mode))
                .append(Component.text(".")));
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("0", "1", "2", "3", "s", "c", "a", "sp");
        }

        return List.of();
    }

    /**
     * Converts a gamemode argument to a {@link GameMode}.
     *
     * @param mode The string provided by the user
     * @return The corresponding GameMode, or null if invalid
     */
    private GameMode getGamemode(String mode) {
        return switch (mode) {
            case "0", "s", "survival", "survie" -> GameMode.SURVIVAL;
            case "1", "c", "creative", "creatif" -> GameMode.CREATIVE;
            case "2", "a", "adventure", "aventure" -> GameMode.ADVENTURE;
            case "3", "sp", "spectator", "spectateur" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    private Component getModeName(GameMode gameMode) {
        return switch (gameMode) {
            case SURVIVAL -> Component.translatable("gameMode.survival");
            case CREATIVE -> Component.translatable("gameMode.creative");
            case ADVENTURE -> Component.translatable("gameMode.adventure");
            case SPECTATOR -> Component.translatable("gameMode.spectator");
        };
    }
    
}
