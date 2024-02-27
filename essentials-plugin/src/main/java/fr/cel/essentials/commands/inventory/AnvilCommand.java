package fr.cel.essentials.commands.inventory;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class AnvilCommand extends AbstractCommand {

    public AnvilCommand() {
        super("essentials:anvil", true, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        ((Player)sender).openInventory(Bukkit.createInventory(null, InventoryType.ANVIL));
    }

}
