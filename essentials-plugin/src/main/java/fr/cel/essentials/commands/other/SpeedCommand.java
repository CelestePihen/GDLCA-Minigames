package fr.cel.essentials.commands.other;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpeedCommand extends AbstractCommand {

    public SpeedCommand() {
        super("essentials:speed", false, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {

        if (args.length == 0 || args.length >= 3) {
            sendMessageWithPrefix(sender, "La commande est : /speed <vitesse> <joueur>");
            return;
        }

        if (args.length == 1 && sender instanceof Player player) {
            int speed;
            try {
                speed = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sendMessageWithPrefix(player, "Merci de mettre un nombre entre 1 et 10. Vitesse initial : 2");
                return;
            }

            if (speed < 1 || speed > 10) {
                sendMessageWithPrefix(player, "Merci de mettre un nombre entre 1 et 10. Vitesse initial : 2");
                return;
            }

            if (player.isFlying()) player.setFlySpeed((float) speed / 10);
            else player.setWalkSpeed((float) speed / 10);
            sendMessageWithPrefix(player, "Ta vitesse a été mis à " + speed + ".");
            return;

        }

        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);

            if (isPlayerOnline(target, sender)) {
                int speed;
                try {
                    speed = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sendMessageWithPrefix(sender, "Merci de mettre un nombre entre 1 et 10. Vitesse initial : 2");
                    return;
                }

                if (speed < 1 || speed > 10) {
                    sendMessageWithPrefix(sender, "Merci de mettre un nombre entre 1 et 10. Vitesse initial : 2");
                    return;
                }

                if (target.isFlying()) target.setFlySpeed((float) speed / 10);
                else target.setWalkSpeed((float) speed / 10);
                sendMessageWithPrefix(target, "Ta vitesse a été mise à " + speed + ".");
                sendMessageWithPrefix(sender, "La vitesse de " + target.getName() + " a été mise à " + speed + ".");
            }
        }
        
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return IntStream.range(1, 10).mapToObj(Integer::toString).collect(Collectors.toList());
        }
        return null;
    }

}