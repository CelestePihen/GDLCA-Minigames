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
        double x = player.getLocation().getX();
        double z = player.getLocation().getZ();

        player.teleport(new Location(player.getWorld(), x, player.getLocation().getWorld().getHighestBlockYAt((int) x, (int) z), z));
        player.sendMessage(main.getPrefix() + "Vous avez été téléporté(e) à la surface.");
    }
    
}
