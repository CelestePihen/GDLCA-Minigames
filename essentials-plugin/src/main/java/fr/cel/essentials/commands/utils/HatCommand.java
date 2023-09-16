package fr.cel.essentials.commands.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class HatCommand extends AbstractCommand {

    public HatCommand(Essentials main) {
        super(main, "hat");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        
        if (args.length == 0) {
            ItemStack it = player.getInventory().getItemInMainHand();
            it.setAmount(1);
            player.getInventory().setHelmet(it);
            sendMessageWithPrefix(player, "Tu as mis sur ta tête : " + it.getType().name());
        }
        

    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}

}