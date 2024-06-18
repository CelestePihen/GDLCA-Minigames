package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.gameapi.utils.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BroadcastCommand extends AbstractCommand {

    public BroadcastCommand() {
        super("essentials:broadcast", false, true);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length >= 1) {
            StringBuilder bc = new StringBuilder();
            for (String part : args) bc.append(part).append(" ");
            Bukkit.broadcastMessage(ChatUtility.format("&c[Broadcast]&r&d " + bc));
        } else {
            sendMessageWithPrefix(sender, "La commande est : /broadcast <message>");
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] strings) {
        return null;
    }

}
