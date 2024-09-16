package fr.cel.hub.commands;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.hub.inventory.event.EventInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EventCommands extends AbstractCommand {

    public EventCommands() {
        super("hub:event", true, true);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            this.sendMessageWithPrefix(player, "La commande est : /event <hub>");
            return;
        }

        if (args[0].equalsIgnoreCase("hub") && GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) {
            GameAPI.getInstance().getInventoryManager().openInventory(new EventInventory(), player);
        }

    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("hub");
        }
        return null;
    }

}