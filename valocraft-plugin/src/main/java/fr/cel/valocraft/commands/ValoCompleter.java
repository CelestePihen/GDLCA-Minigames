package fr.cel.valocraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ValoCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        List<String> arguments = new ArrayList<>();

        if (!(sender instanceof Player)) return arguments;

        if (args.length == 1) {
            arguments.add("start");
            arguments.add("list");
            arguments.add("listplayer");
            arguments.add("reload");
        }

        if (arguments != null) arguments = adopt(args[args.length - 1], arguments);
        return arguments;
    }

    private List<String> adopt(String last, List<String> variants) {
        List<String> variantsList = new ArrayList<>(variants);
        for (String variant : variantsList) if (!variant.startsWith(last)) variants.remove(variant);
        return variants;
    }

}