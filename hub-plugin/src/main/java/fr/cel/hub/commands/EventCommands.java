package fr.cel.hub.commands;

import fr.cel.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EventCommands extends AbstractCommand {

    private Inventory inventory;

    public EventCommands(Hub main) {
        super(main, "event", true);
    }

    @Override
    public void onExecute(Player player, String[] args) {

        if (args.length == 0) {
            this.sendMessageWithPrefix(player, "La commande est : /event <hub>");
            return;
        }

        if (main.getPlayerManager().containsPlayerInHub(player) && args[0].equalsIgnoreCase("hub")) {
            player.openInventory(main.getInventoryManager().getInventory("event"));
            return;
        }

        if (args[0].equalsIgnoreCase("institution")) {
            if (args.length != 2) {
                this.sendMessageWithPrefix(player, "Tu as été téléporté(e) dans l'Institution.");
                main.getPlayerManager().sendPlayerToInstitution(player);
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                this.sendMessageWithPrefix(player, "Ce joueur n'est pas connecté ou n'existe pas.");
            } else {
                main.getPlayerManager().sendPlayerToInstitution(target);
            }
        }

    }

    @Override
    public void onTabComplete(Player player, String label, String[] args) {
        if (args.length == 1) {
            arguments.add("hub");
        }
    }

}