package fr.cel.essentials.commands.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class TopCommand extends AbstractCommand {

    public TopCommand(Essentials main) {
        super(main, "top");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        int x = (int) player.getLocation().getX();
        int z = (int) player.getLocation().getZ();

        player.teleport(new Location(player.getWorld(), x, player.getLocation().getWorld().getHighestBlockYAt(x, z), z));
        sendMessageWithPrefix(player, "Vous avez été téléporté(e) à la surface.");
    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}
    
}
