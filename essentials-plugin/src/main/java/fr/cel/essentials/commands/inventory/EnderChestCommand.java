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
        
        if (args.length <= 0) {
            player.openInventory(player.getEnderChest());
            player.sendMessage(main.getPrefix() + "Vous avez ouvert votre coffre de l'end.");
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                player.openInventory(target.getEnderChest());
                player.sendMessage(main.getPrefix() + "Vous avez ouvert le coffre de l'end de " + target.getName() + ".");
                return;
            } else {
                player.sendMessage(main.getPrefix() + "Ce joueur n'existe pas ou n'est pas connecté.");
                return;
            }
        }
        
    }
    
}