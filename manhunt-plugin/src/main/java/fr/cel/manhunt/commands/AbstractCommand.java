package fr.cel.manhunt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.cel.manhunt.ManHunt;

public abstract class AbstractCommand implements CommandExecutor {
    
    protected final ManHunt main;

    public AbstractCommand(ManHunt main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Vous devez etre un joueur pour faire cette commande.");
            return false;
        }

        onExecute((Player) sender, args);

        return false;
    }

    protected abstract void onExecute(Player player, String[] args);
    
}
