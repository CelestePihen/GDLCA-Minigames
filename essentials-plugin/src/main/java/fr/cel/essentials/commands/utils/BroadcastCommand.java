package fr.cel.essentials.commands.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;
import fr.cel.hub.utils.ChatUtility;

public class BroadcastCommand extends AbstractCommand {

    public BroadcastCommand(Essentials main) {
        super(main, "broadcast");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        if (args.length >= 1) {
            final StringBuilder bc = new StringBuilder();

            for (String part : args) bc.append(part).append(" ");

            Bukkit.broadcastMessage(ChatUtility.format("&c[Broadcast]&r&d " + bc));
        } else {
            sendMessageWithPrefix(player, "La commande est : /broadcast <message>");
        }
    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}

}
