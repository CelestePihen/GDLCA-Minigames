package fr.cel.valocraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValoCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> arguments = new ArrayList<>();

        if (!(sender instanceof Player) || args.length == 0) {
            return arguments;
        }

        if (args.length == 1) {
            arguments.addAll(Arrays.asList("start", "list", "listplayer", "reload", "setround"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setround")) {
            arguments.addAll(Arrays.asList("red", "blue"));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setround")) {
            for (int i = 1; i <= 9; i++) {
                arguments.add(String.valueOf(i));
            }
        }

        String lastArg = args[args.length - 1];
        arguments.removeIf(completion -> !completion.startsWith(lastArg));

        return arguments;
    }

}