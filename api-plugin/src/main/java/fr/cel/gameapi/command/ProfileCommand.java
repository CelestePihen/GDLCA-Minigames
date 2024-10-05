package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.ProfileInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ProfileCommand extends AbstractCommand {

    public ProfileCommand() {
        super("gameapi:profile", true, false);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        GameAPI.getInstance().getInventoryManager().openInventory(new ProfileInventory(player), player);
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}