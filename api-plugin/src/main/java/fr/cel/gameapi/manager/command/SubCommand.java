package fr.cel.gameapi.manager.command;

import fr.cel.gameapi.GameAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * All subcommands have to implement this interface
 */
public interface SubCommand {

    /**
     * Name of the subcommand
     * @return Returns the arg that the sender has to write
     */
    String getName();

    /**
     * Description of the subcommand
     * @return Returns the description that will be shown in the help list
     */
    String getDescription();

    /**
     * Usage of the subcommand
     * @return Returns to the sender how to use this subcommand
     */
    String getUsage();

    /**
     * Specify if only a player can execute this subcommand
     * @return If true, only the player can execute this subcommand. Otherwise, console AND players can execute it.
     */
    boolean isPlayerOnly();

    /**
     * Return if the sender of the command has the right to execute this subcommand
     * @param sender The sender of the (sub)command
     * @return True, if the sender has the permission. Otherwise, false
     */
    boolean hasPermission(CommandSender sender);

    void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args);

    default List<String> tab(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        return List.of();
    }

    @Contract("null, _ -> false; !null, _ -> true")
    default boolean isPlayerOnline(@Nullable Player player, @NotNull CommandSender sender) {
        if (player == null) {
            sender.sendMessage(GameAPI.getPrefix().append(Component.text("Le joueur n'existe pas ou n'est pas connecté.")));
            return false;
        }
        return true;
    }

}