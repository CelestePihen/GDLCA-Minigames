package fr.cel.essentials.commands.other;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class SpeedCommand extends AbstractCommand {

    public SpeedCommand(Essentials main) {
        super(main, "speed");
    }

    @Override
    protected void onExecute(Player player, String[] args) {

        if (args.length == 0 || args.length >= 3) {
            sendMessageWithPrefix(player, "La commande est : /speed <vitesse> <joueur>");
            return;
        }

        if (args.length == 1) {
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
                sendMessageWithPrefix(player, "Ce joueur n'existe pas ou n'est pas connecté.");
                return;
            }

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

            if (target.isFlying()) target.setFlySpeed((float) speed / 10);
            else target.setWalkSpeed((float) speed / 10);
            sendMessageWithPrefix(target, "Ta vitesse a été mise à " + speed + ".");
            sendMessageWithPrefix(player, "La vitesse de " + target.getName() + " a été mise à " + speed + ".");
        }
        
    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}

}