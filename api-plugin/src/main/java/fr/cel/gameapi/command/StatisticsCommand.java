package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.StatisticsInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StatisticsCommand extends AbstractCommand {

    public StatisticsCommand() {
        super("gameapi:statistics", true, false);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        GameAPI.getInstance().getInventoryManager().openInventory(new StatisticsInventory(), player);
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return List.of();
    }

}
