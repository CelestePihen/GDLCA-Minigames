package fr.cel.gameapi.manager.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractCommandSub extends AbstractCommand {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    /**
     * Recommended if you want to create a command with sub commands
     * @param permission The permission the player has to have to execute the command
     * @param needPlayer If true, the console can't do this command. Otherwise false
     * @param permissionRequired If true, then the permission is required for the player who executes the command.
     *                           Otherwise false and every player can execute this command
     */
    public AbstractCommandSub(String permission, boolean needPlayer, boolean permissionRequired) {
        super(permission, needPlayer, permissionRequired);
    }

    /**
     * Register a subcommand for this command
     * @param subCommand An instance of the subcommand
     * @see SubCommand
     */
    protected void registerSubCommand(SubCommand subCommand) {
        this.subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        SubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub == null) {
            sendMessageWithPrefix(sender, Component.text("Sous-commande inconnue.", NamedTextColor.RED));
            sendHelp(sender);
            return;
        }

        if (sub.isPlayerOnly() && !(sender instanceof Player)) {
            sendMessageWithPrefix(sender, Component.text("Cette commande est réservée aux joueurs.", NamedTextColor.RED));
            return;
        }

        if (!sub.hasPermission(sender)) {
            sendMessageWithPrefix(sender, Component.text("Tu n’as pas la permission pour cette commande.", NamedTextColor.RED));
            return;
        }

        sub.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    }


    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) return new ArrayList<>(subCommands.keySet());

        SubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub != null) return sub.tab(player, Arrays.copyOfRange(args, 1, args.length));

        return List.of();
    }

    /**
     * Send the available subcommands list to the sender
     * @param sender The sender of the (sub)command
     */
    private void sendHelp(CommandSender sender) {
        sendMessageWithPrefix(sender, Component.text("Liste des sous-commandes disponibles :"));
        for (SubCommand sub : subCommands.values())
            sender.sendMessage(Component.text(sub.getUsage() + " - " + sub.getDescription(), NamedTextColor.YELLOW));
    }
}
