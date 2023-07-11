package fr.cel.essentials.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;

public abstract class AbstractCommand implements CommandExecutor {

    protected final Essentials main;

    private final String permission;

    public AbstractCommand(Essentials main, String permission) {
        this.main = main;
        this.permission = permission;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Vous devez etre un joueur pour effectuer cette commande.");
            return false;
        }

        final Player player = (Player) sender;

        if (player.hasPermission("essentials." + permission)) {
            onExecute(player, args);
        } else {
            player.sendMessage(main.getPrefix() + "Vous n'avez pas la permission d'utiliser cette commande.");
        }

        return false;
    }

    protected abstract void onExecute(Player player, String[] args);
    
}