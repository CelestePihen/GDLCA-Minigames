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

        if (args.length <= 0 || args.length >= 3) {
            player.sendMessage(main.getPrefix() + "La commande est : /speed <vitesse> <joueur>");
            return;
        }

        if (args.length == 1) {
            int speed;
            try {
                speed = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(main.getPrefix() + "Merci de mettre un nombre entre 1 et 10 : /speed 1-10");
                return;
            }

            if (speed < 1 || speed > 10) {
                player.sendMessage(main.getPrefix() + "Merci de mettre un nombre entre 1 et 10 : /speed 1-10");
                return;
            }

            if (player.isFlying()) player.setFlySpeed((float) speed / 10);
            else player.setWalkSpeed((float) speed / 10);
            player.sendMessage(main.getPrefix() + "Ta vitesse a été mis à " + speed + ".");
            return;

        }

        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage(main.getPrefix() + "Ce joueur n'existe pas ou n'est pas connecté.");
                return;
            }

            int speed;
            try {
                speed = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(main.getPrefix() + "Merci de mettre un nombre entre 1 et 10 : /speed 1-10");
                return;
            }

            if (speed < 1 || speed > 10) {
                player.sendMessage(main.getPrefix() + "Merci de mettre un nombre entre 1 et 10 : /speed 1-10");
                return;
            }

            if (target.isFlying()) target.setFlySpeed((float) speed / 10);
            else target.setWalkSpeed((float) speed / 10);
            target.sendMessage(main.getPrefix() + "Ta vitesse a été mise à " + speed + ".");
            player.sendMessage(main.getPrefix() + "La vitesse de " + target.getName() + " a été mise à " + speed + ".");
            return;
            
        }
        
    }
    
}