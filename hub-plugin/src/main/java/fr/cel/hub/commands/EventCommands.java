package fr.cel.hub.commands;

import fr.cel.hub.Hub;
import fr.cel.hub.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EventCommands extends AbstractCommand {

    private Inventory inventory;

    public EventCommands(Hub main) {
        super(main, "event");
    }

    @Override
    public void onExecute(Player player, String[] args) {
        
        if (args.length == 0) {
            this.sendMessageWithPrefix(player, "La commande est :/event <music/musique>");
            return;
        }

        if (main.getPlayerManager().containsPlayerInHub(player) && args[0].equalsIgnoreCase("hub")) {
            if (inventory == null) inventory = createEventInventory();
            player.openInventory(inventory);
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

    // Permet de créer l'inventaire qui permet de gérer les événements sur le serveur
    private Inventory createEventInventory() {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Événements"));

        inv.setItem(10, new ItemBuilder(Material.JUKEBOX).setDisplayName(Component.text("Mettre de la Musique").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toItemStack());
        inv.setItem(13, new ItemBuilder(Material.FIREWORK_ROCKET).setDisplayName(Component.text("Activer le Système").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toItemStack());
        inv.setItem(16, new ItemBuilder(Material.PLAYER_HEAD).setDisplayName(Component.text("???").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toItemStack());

        return inv;
    }

}