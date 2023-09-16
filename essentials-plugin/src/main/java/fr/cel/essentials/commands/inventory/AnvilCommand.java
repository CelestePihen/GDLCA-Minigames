package fr.cel.essentials.commands.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class AnvilCommand extends AbstractCommand {

    public AnvilCommand(Essentials main) {
        super(main, "anvil");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        player.openInventory(Bukkit.createInventory(null, InventoryType.ANVIL));
    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}

}
