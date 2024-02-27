package fr.cel.essentials.commands.inventory;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderChestCommand extends AbstractCommand {

    public EnderChestCommand() {
        super("essentials:ec", true, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            player.openInventory(player.getEnderChest());
            sendMessageWithPrefix(player, "Vous avez ouvert votre coffre de l'ender.");
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                player.openInventory(target.getEnderChest());
                sendMessageWithPrefix(player, "Vous avez ouvert le coffre de l'ender de " + target.getName() + ".");
            } else {
                sendMessageWithPrefix(player, "Ce joueur n'existe pas ou n'est pas connecté.");
            }
        }
        
    }

}