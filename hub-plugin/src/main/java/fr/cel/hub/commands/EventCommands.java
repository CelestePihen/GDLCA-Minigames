package fr.cel.hub.commands;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.hub.Hub;
import fr.cel.hub.inventory.MinigamesInventory;
import fr.cel.hub.inventory.event.EventInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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

        if (GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player) && args[0].equalsIgnoreCase("hub")) {
            GameAPI.getInstance().getInventoryManager().openInventory(new EventInventory(Hub.getInstance()), player);
        }

    }

}