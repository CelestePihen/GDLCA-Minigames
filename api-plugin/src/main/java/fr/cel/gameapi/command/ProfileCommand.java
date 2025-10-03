package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.inventory.ProfileInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProfileCommand extends AbstractCommand {

    public ProfileCommand() {
        super("gameapi:profile", true, false);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        Player player = (Player) sender;
        GameAPI.getInstance().getInventoryManager().openInventory(new ProfileInventory(player), player);
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}