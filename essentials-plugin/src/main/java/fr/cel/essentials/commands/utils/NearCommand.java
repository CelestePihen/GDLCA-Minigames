package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NearCommand extends AbstractCommand {

    public NearCommand() {
        super("essentials:near", false, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0 || args.length > 3) {
            sendMessageWithPrefix(sender, "La commande est : /near <radius> ou /near <radius> <joueur>");
            return;
        }

        if (args.length == 1 && sender instanceof Player player) {
            searchPlayers(player, player, args);
            return;
        }

        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);

            if (isPlayerOnline(target, sender)) {
                searchPlayers(sender, target, args);
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

    private void searchPlayers(CommandSender sender, Player target, String[] args) {
        int radius;
        try {
            radius = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sendMessageWithPrefix(sender, "Merci de mettre un nombre valide. La commande est : /near <radius> ou /near <radius> <joueur>");
            return;
        }

        List<String> playersName = new ArrayList<>();
        List<Entity> Entities = target.getNearbyEntities(radius, radius, radius);
        for (Entity entity : Entities) {
            if (entity instanceof Player pl) {
                playersName.add(pl.getName());
            }
        }

        sendMessageWithPrefix(sender, "Les joueurs proches de vous sont : " + playersName);
    }
    
}