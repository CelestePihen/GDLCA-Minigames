package fr.cel.essentials.commands.inventory;

import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class CraftCommand extends AbstractCommand {

    public CraftCommand(Essentials main) {
        super(main, "craft");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        player.openWorkbench(player.getLocation(), true);
    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}

}