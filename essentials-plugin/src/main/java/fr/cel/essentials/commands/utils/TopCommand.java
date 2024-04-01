package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommand extends AbstractCommand {

    public TopCommand() {
        super("essentials:top", true, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        int x = (int) player.getLocation().getX();
        int z = (int) player.getLocation().getZ();

        player.teleport(new Location(player.getWorld(), x + 0.5D, player.getWorld().getHighestBlockYAt(x, z) + 1, z + 0.5D));
        sendMessageWithPrefix(player, "Vous avez été téléporté(e) à la surface.");
    }
    
}
