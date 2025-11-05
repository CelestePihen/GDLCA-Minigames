package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.manager.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BarrierCommand extends AbstractCommand {

    public BarrierCommand() {
        super("essentials:barrier", false, true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String[] args) {
        ItemStack barrierItem = new ItemStack(Material.BARRIER);

        if (args.length == 0) {
            if (sender instanceof Player player) {
                giveBarrier(player, barrierItem, Component.text("Un bloc de barrière t'a été donné(e)."));
            } else {
                sendMessageWithPrefix(sender, Component.text("Usage: /barrier <player>"));
            }

            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (isPlayerOnline(target, sender)) {
                giveBarrier(target, barrierItem, Component.text("Tu as donné un bloc de barrière à " + target.getName()));
            }
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    private void giveBarrier(Player player, ItemStack barrier, Component message) {
        player.getInventory().addItem(barrier);
        sendMessageWithPrefix(player, message);
    }

}