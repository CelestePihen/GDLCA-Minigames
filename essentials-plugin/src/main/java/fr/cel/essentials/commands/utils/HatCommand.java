package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HatCommand extends AbstractCommand {

    public HatCommand() {
        super("essentials:hat", true, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            ItemStack it = player.getInventory().getItemInMainHand();
            if (it.getType() == Material.AIR) return;
            it.setAmount(1);
            player.getInventory().setHelmet(it);
            sendMessageWithPrefix(player, "Tu as mis sur ta tête : " + it.getType().name());
        }
        

    }

}