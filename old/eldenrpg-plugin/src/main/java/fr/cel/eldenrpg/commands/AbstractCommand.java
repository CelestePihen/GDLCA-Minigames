package fr.cel.eldenrpg.commands;

import fr.cel.eldenrpg.EldenRPG;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {
    
    protected final EldenRPG main;
    private final String commandName;
    protected List<String> arguments;

    public AbstractCommand(EldenRPG main, String commandName) {
        this.main = main;
        this.commandName = commandName;
        this.arguments = new ArrayList<>();

        main.getCommand(commandName).setExecutor(this);
        main.getCommand(commandName).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Vous devez être un joueur pour faire cette commande.");
            return false;
        }

        if (player.hasPermission("eldenrpg." + commandName)) {
            onExecute((Player) sender, args);
            return true;
        } else {
            player.sendMessage("Vous n'avez pas la permission d'utiliser cette commande.");
            return false;
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return arguments;

        onTabComplete(label, args);

        if (arguments != null) arguments = adopt(args[args.length - 1], arguments);
        return arguments;
    }

    protected abstract void onExecute(Player player, String[] args);

    protected abstract void onTabComplete(String label, String[] args);

    private List<String> adopt(String last, List<String> variants) {
        List<String> variantsList = new ArrayList<>(variants);
        for (String variant : variantsList) if (!variant.startsWith(last)) variants.remove(variant);
        return variants;
    }
    
}
