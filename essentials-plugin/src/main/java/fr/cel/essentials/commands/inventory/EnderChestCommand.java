package fr.cel.essentials.commands.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class EnderChestCommand extends AbstractCommand {

    public EnderChestCommand(Essentials main) {
        super(main, "ec");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        
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

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}

}