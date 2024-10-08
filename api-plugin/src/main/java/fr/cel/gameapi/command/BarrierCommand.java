package fr.cel.gameapi.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BarrierCommand extends AbstractCommand {

    public BarrierCommand() {
        super("gameapi:barrier", false, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        ItemStack barrierItem = new ItemStack(Material.BARRIER);

        if (args.length == 0) {
            if (sender instanceof Player player) {
                giveBarrier(player, barrierItem, "Un bloc de barrière vous a été donné(e).");
            } else {
                sendMessageWithPrefix(sender, "Vous ne pouvez pas vous donner un bloc de barrière...");
            }
        }

        else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (isPlayerOnline(target, sender)) {
                giveBarrier(target, barrierItem, "Vous avez donné un bloc de barrière à " + target.getName());
            }
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    private void giveBarrier(Player player, ItemStack barrier, String message) {
        player.getInventory().addItem(barrier);
        sendMessageWithPrefix(player, message);
    }

}