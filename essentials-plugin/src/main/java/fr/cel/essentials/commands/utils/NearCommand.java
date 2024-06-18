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
        Player player = (Player) sender;

        if (args.length == 0 || args.length > 3) {
            sendMessageWithPrefix(player, "La commande est : /near <radius> ou /near <radius> <joueur>");
            return;
        }

        if (args.length == 1 && !isPlayer(sender)) {
            searchPlayers(player, player, args);
            return;
        }

        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);

            if (isPlayerOnline(target, sender)) {
                searchPlayers(player, target, args);
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

    private void searchPlayers(Player player, Player target, String[] args) {
        int radius;
        try {
            radius = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sendMessageWithPrefix(player, "Merci de mettre un nombre valide. La commande est : /near <radius> ou /near <radius> <joueur>");
            return;
        }

        List<String> playersName = new ArrayList<>();
        for (Entity entity : target.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player pl) {
                playersName.add(pl.getName());
            }
        }

        sendMessageWithPrefix(player, "Les joueurs proches de vous sont : " + playersName);
    }
    
}