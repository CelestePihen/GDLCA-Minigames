package fr.cel.essentials.commands.inventory;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EnderChestCommand extends AbstractCommand {

    // TODO faire en sorte que ça marche côté console
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

            if (isPlayerOnline(target, sender)) {
                player.openInventory(target.getEnderChest());
                sendMessageWithPrefix(player, "Vous avez ouvert le coffre de l'ender de " + target.getName() + ".");
            }
        }
        
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}