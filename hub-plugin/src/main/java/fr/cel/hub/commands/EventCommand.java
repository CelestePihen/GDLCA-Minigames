package fr.cel.hub.commands;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.command.AbstractCommand;
import fr.cel.hub.inventory.event.CelebrationEvent;
import fr.cel.hub.inventory.event.EventInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EventCommand extends AbstractCommand {

    public EventCommand() {
        super("hub:event", true, true);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        Player player = (Player) sender;

        if (!GameAPI.getInstance().getPlayerManager().containsPlayerInHub(player)) {
            sendMessageWithPrefix(player, Component.text("Vous devez être dans le Hub pour pouvoir faire cette commande."));
            return;
        }

        if (args.length == 0) {
            sendMessageWithPrefix(player, Component.text("La commande est : /event <hub|celebration>"));
            return;
        }

        if (args[0].equalsIgnoreCase("hub")) {
            GameAPI.getInstance().getInventoryManager().openInventory(new EventInventory(), player);
        }

        else if (args[0].equalsIgnoreCase("celebration")) {
            GameAPI.getInstance().getInventoryManager().openInventory(new CelebrationEvent(), player);
        }

        else {
            sendMessageWithPrefix(player, Component.text("La commande est : /event <hub | celebration>"));
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("hub", "celebration");
        }
        return null;
    }

}