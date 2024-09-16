package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HatCommand extends AbstractCommand {

    // TODO faire en sorte que ça marche côté console
    public HatCommand() {
        super("essentials:hat", true, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        ItemStack it = player.getInventory().getItemInMainHand();
        if (it.getType() == Material.AIR) {
            sendMessageWithPrefix(player, "Vous ne pouvez pas ne rien mettre sur votre tête...");
            return;
        }

        it.setAmount(1);
        player.getInventory().setHelmet(it);
        sendMessageWithPrefix(player, "Tu as mis sur ta tête : " + it.getType().name());
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] strings) {
        return null;
    }

}