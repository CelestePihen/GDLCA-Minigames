package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public abstract class AbstractCommand implements TabExecutor {

    private final String permission;
    private final boolean needPlayer;
    private final boolean permissionRequired;

    public AbstractCommand(String permission, boolean needPlayer, boolean permissionRequired) {
        this.permission = permission;
        this.needPlayer = needPlayer;
        this.permissionRequired = permissionRequired;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (needPlayer && !(sender instanceof Player)) {
            sendMessageWithPrefix(sender, Component.text("Tu dois etre un joueur pour effectuer cette commande."));
            return false;
        }

        else if (!sender.hasPermission(permission) && isPermissionRequired()) {
            sendMessageWithPrefix(sender, Component.text("Tu n'as pas la permission d'effectuer cette commande."));
            return false;
        }

        onExecute(sender, args);
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (needPlayer && !(sender instanceof Player)) {
            return null;
        } else if (!sender.hasPermission(permission) && isPermissionRequired()) {
            return null;
        }

        return onTabComplete((Player) sender, args);
    }

    /**
     * This method is executed when the sender runs the command.
     * @param sender The sender executing the command
     * @param args The arguments provided by the sender
     */
    protected abstract void onExecute(@NotNull CommandSender sender, String @NotNull [] args);

    /**
     * Provides possible tab-completion arguments for the player when typing the command.
     * @param player The player requesting tab-completion
     * @param args The current arguments typed by the player
     * @return A list of possible arguments for tab-completion
     */
    protected abstract List<String> onTabComplete(Player player, String[] args);

    /**
     * Checks if a player is online.
     * @param player The player to check
     * @param sender The sender to notify if the player is null or offline
     * @return Returns true if the player is online; otherwise, sends a message to the sender and returns false
     */
    @Contract("null, _ -> false; !null, _ -> true")
    protected boolean isPlayerOnline(@Nullable Player player, CommandSender sender) {
        if (player == null) {
            sendMessageWithPrefix(sender, Component.text("Le joueur n'existe pas ou n'est pas connecté."));
            return false;
        }
        return true;
    }

    /**
     * Sends a message to the sender with the server prefix.
     * @param sender The recipient of the message
     * @param message The message to send
     */
    protected void sendMessageWithPrefix(CommandSender sender, Component message) {
        sender.sendMessage(GameAPI.getPrefix().append(message.colorIfAbsent(NamedTextColor.WHITE)));
    }
    
}