package fr.cel.essentials.commands.inventory;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CraftCommand extends AbstractCommand {

    public CraftCommand() {
        super("essentials:craft", true, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        player.openWorkbench(player.getLocation(), true);
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] strings) {
        return null;
    }

}