package fr.cel.essentials.commands.other;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand extends AbstractCommand {

    public SpeedCommand() {
        super("essentials:speed", false, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0 || args.length >= 3) {
            sendMessageWithPrefix(sender, "La commande est : /speed <vitesse> <joueur>");
            return;
        }

        if (args.length == 1 && isPlayer(sender)) {
            int speed;
            try {
                speed = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sendMessageWithPrefix(player, "Merci de mettre un nombre entre 1 et 10 : /speed 1-10");
                return;
            }

            if (speed < 1 || speed > 10) {
                sendMessageWithPrefix(player, "Merci de mettre un nombre entre 1 et 10 : /speed 1-10");
                return;
            }

            if (player.isFlying()) player.setFlySpeed((float) speed / 10);
            else player.setWalkSpeed((float) speed / 10);
            sendMessageWithPrefix(player, "Ta vitesse a été mis à " + speed + ".");
            return;

        }

        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sendMessageWithPrefix(sender, "Ce joueur n'existe pas ou n'est pas connecté.");
                return;
            }

            int speed;
            try {
                speed = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sendMessageWithPrefix(sender, "Merci de mettre un nombre entre 1 et 10 : /speed 1-10");
                return;
            }

            if (speed < 1 || speed > 10) {
                sendMessageWithPrefix(sender, "Merci de mettre un nombre entre 1 et 10 : /speed 1-10");
                return;
            }

            if (target.isFlying()) target.setFlySpeed((float) speed / 10);
            else target.setWalkSpeed((float) speed / 10);
            sendMessageWithPrefix(target, "Ta vitesse a été mise à " + speed + ".");
            sendMessageWithPrefix(sender, "La vitesse de " + target.getName() + " a été mise à " + speed + ".");
        }
        
    }

}