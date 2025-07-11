package fr.cel.decorationsplugin.command;

import fr.cel.decorationsplugin.DecorationsPlugin;
import fr.cel.decorationsplugin.manager.Chair;
import fr.cel.decorationsplugin.manager.ChairManager;
import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChairCommand extends AbstractCommand {

    private final ChairManager chairManager;

    public ChairCommand(ChairManager chairManager) {
        super("decorations:chairs", true, true);
        this.chairManager = chairManager;
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> addChair(player, args);
            case "remove" -> removeChair(player, args);
            case "list" -> listChairs(player);
            case "reload" -> reloadChairs(player);
            default -> sendHelp(player);
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("add", "remove", "list", "reload");
        }

        return List.of();
    }

    private void addChair(Player player, String[] args) {
        Location location;

        Block blockPlayerLooking = player.getTargetBlockExact(5);
        if (blockPlayerLooking != null) {
            location = blockPlayerLooking.getLocation();
        } else {
            location = parseLocation(player, args);
        }

        if (location == null) {
            player.sendMessage(DecorationsPlugin.getPrefix() + "§cCoordonnées invalides !");
            return;
        }

        if (!location.getBlock().getType().name().contains("STAIRS") && location.getBlock().getType() != Material.BARRIER) {
            player.sendMessage(DecorationsPlugin.getPrefix() + "§cCe bloc n'est pas un escalier ou une barrière invisible !");
            return;
        }

        if (chairManager.addChair(location)) {
            player.sendMessage(DecorationsPlugin.getPrefix() + "§aChaise ajoutée avec succès aux coordonnées : " + formatLocation(location));
            chairManager.saveChairs();
        } else {
            player.sendMessage(DecorationsPlugin.getPrefix() + "§cCette position contient déjà une chaise !");
        }
    }

    private void removeChair(Player player, String[] args) {
        Location location;

        Block blockPlayerLooking = player.getTargetBlockExact(5);
        if (blockPlayerLooking != null) {
            location = blockPlayerLooking.getLocation();
        } else {
            location = parseLocation(player, args);
        }

        if (location == null) {
            player.sendMessage(DecorationsPlugin.getPrefix() + "§cCoordonnées invalides !");
            return;
        }

        if (chairManager.removeChair(location)) {
            player.sendMessage(DecorationsPlugin.getPrefix() + "§aChaise supprimée avec succès !");
            chairManager.saveChairs();
        } else {
            player.sendMessage(DecorationsPlugin.getPrefix() + "§cAucune chaise trouvée à cette position !");
        }
    }

    private void listChairs(Player player) {
        if (chairManager.getChairCount() == 0) {
            player.sendMessage(DecorationsPlugin.getPrefix() + "§eAucune chaise configurée.");
            return;
        }

        player.sendMessage("§6=== Liste des chaises (" + chairManager.getChairCount() + ") ===");
        int i = 1;
        for (Chair chair : chairManager.getAllChairs()) {
            Location loc = chair.getLocation();
            String status = chair.isOccupied() ? "§c[OCCUPÉE]" : "§a[LIBRE]";
            player.sendMessage(String.format("§e%d. %s x:%d y:%d z:%d (%s)",
                    i++, status, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()));
        }
    }

    private void reloadChairs(Player player) {
        chairManager.reload();
        player.sendMessage(DecorationsPlugin.getPrefix() + "§aConfiguration rechargée ! " + chairManager.getChairCount() + " chaises chargées.");
    }

    private Location parseLocation(Player player, String[] args) {
        if (args.length < 4) return null;

        try {
            World world = player.getWorld();

            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);

            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatLocation(Location loc) {
        return String.format("X:%d Y:%d Z:%d (%s)", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
    }

    private void sendHelp(Player player) {
        player.sendMessage("§6=== Commandes ChairStairs ===");
        player.sendMessage("§e/chairstairs add [x] [y] [z] [monde] §7- Ajouter une chaise persistante");
        player.sendMessage("§e/chairstairs remove [x] [y] [z] [monde] §7- Supprimer une chaise");
        player.sendMessage("§e/chairstairs list §7- Liste des chaises et leur statut");
        player.sendMessage("§e/chairstairs save §7- Sauvegarder manuellement");
        player.sendMessage("§e/chairstairs reload §7- Recharger la configuration");
    }

}