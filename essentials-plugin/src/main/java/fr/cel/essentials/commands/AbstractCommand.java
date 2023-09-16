package fr.cel.essentials.commands;

import fr.cel.essentials.Essentials;
import fr.cel.hub.Hub;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    protected final Essentials main;
    protected List<String> arguments;

    public AbstractCommand(Essentials main, String commandName) {
        this.main = main;
        this.arguments = new ArrayList<>();

        main.getCommand(commandName).setExecutor(this);
        main.getCommand(commandName).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Vous devez etre un joueur pour faire cette commande.");
            return false;
        }

        onExecute(player, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return arguments;

        onTabComplete((Player) sender, label, args);

        if (arguments != null) arguments = adopt(args[args.length - 1], arguments);
        return arguments;
    }

    protected abstract void onExecute(Player player, String[] args);

    protected abstract void onTabComplete(Player player, String label, String[] args);

    private List<String> adopt(String last, List<String> variants) {
        List<String> variantsList = new ArrayList<>(variants);
        for (String variant : variantsList) if (!variant.startsWith(last)) variants.remove(variant);
        return variants;
    }

    /**
     * Permet d'envoyer un message avec le prefix (avec l'API Adventure)
     * @param player Le joueur
     * @param message Le message
     */
    protected void sendMessageWithPrefix(Player player, String message) {
        player.sendMessage(Hub.getHub().getPrefix() + message);
    }

}
