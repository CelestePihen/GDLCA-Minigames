package fr.cel.decorationsplugin.command;

import fr.cel.decorationsplugin.DecorationsPlugin;
import fr.cel.decorationsplugin.manager.Chair;
import fr.cel.decorationsplugin.manager.ChairManager;
import fr.cel.gameapi.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChairCommand extends AbstractCommand {

    private final ChairManager chairManager;

    public ChairCommand(ChairManager chairManager) {
        super("decorations:chairs", true, true);
        this.chairManager = chairManager;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
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
        return null;
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
            player.sendMessage(DecorationsPlugin.getPrefix().append(Component.text("Coordonnées invalides !", NamedTextColor.RED)));
            return;
        }

        if (!location.getBlock().getType().name().contains("STAIRS") && location.getBlock().getType() != Material.BARRIER) {
            player.sendMessage(DecorationsPlugin.getPrefix().append(Component.text("Ce bloc n'est pas un escalier ou une barrière invisible !", NamedTextColor.RED)));
            return;
        }

        if (chairManager.addChair(location)) {
            player.sendMessage(DecorationsPlugin.getPrefix().append(Component.text("Chaise ajoutée avec succès aux coordonnées : " + formatLocation(location), NamedTextColor.GREEN)));
        } else {
            player.sendMessage(DecorationsPlugin.getPrefix().append(Component.text("Cette position contient déjà une chaise !", NamedTextColor.GREEN)));
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
            player.sendMessage(DecorationsPlugin.getPrefix().append(Component.text("Coordonnées invalides !", NamedTextColor.RED)));
            return;
        }

        if (chairManager.removeChair(location)) {
            player.sendMessage(DecorationsPlugin.getPrefix().append(Component.text("Chaise supprimée avec succès !", NamedTextColor.GREEN)));
        } else {
            player.sendMessage(DecorationsPlugin.getPrefix().append(Component.text("Aucune chaise trouvée à cette position !", NamedTextColor.RED)));
        }
    }

    private void listChairs(Player player) {
        if (chairManager.getChairCount() == 0) {
            player.sendMessage(DecorationsPlugin.getPrefix().append(Component.text("Aucune chaise configurée.", NamedTextColor.YELLOW)));
            return;
        }

        player.sendMessage(Component.text("=== Liste des chaises (" + chairManager.getChairCount() + ") ===", NamedTextColor.GOLD));
        int i = 1;
        for (Chair chair : chairManager.getAllChairs()) {
            Location loc = chair.getLocation();

            Component status = chair.isOccupied() ? Component.text("[OCCUPÉE]", NamedTextColor.RED) : Component.text("[LIBRE]", NamedTextColor.GREEN);
            Component message = Component.text(i++ + ". ", NamedTextColor.YELLOW)
                    .append(status)
                    .append(Component.text(" (" + loc.getBlockX() + ", "))
                    .append(Component.text(loc.getBlockY() + ", "))
                    .append(Component.text(loc.getBlockZ() + ") "))
                    .append(Component.text("(" + loc.getWorld().getName() + ")"));

            player.sendMessage(message);
        }
    }

    private void reloadChairs(Player player) {
        chairManager.reload();
        player.sendMessage(DecorationsPlugin.getPrefix().append(Component.text("Configuration rechargée ! " + chairManager.getChairCount() + " chaises chargées.", NamedTextColor.GREEN)));
    }

    private @Nullable Location parseLocation(Player player, String[] args) {
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

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("=== Commandes ChairStairs ===", NamedTextColor.GOLD));
        player.sendMessage(Component.text("/chairstairs add [x] [y] [z] [world]", NamedTextColor.YELLOW).append(Component.text(" - Ajouter une chaise persistante", NamedTextColor.WHITE)));
        player.sendMessage(Component.text("/chairstairs remove [x] [y] [z] [monde]", NamedTextColor.YELLOW).append(Component.text(" - Supprimer une chaise", NamedTextColor.WHITE)));
        player.sendMessage(Component.text("/chairstairs list", NamedTextColor.YELLOW).append(Component.text(" - Liste des chaises et leur statut", NamedTextColor.WHITE)));
        player.sendMessage(Component.text("/chairstairs save", NamedTextColor.YELLOW).append(Component.text(" - Sauvegarder manuellement la configuration", NamedTextColor.WHITE)));
        player.sendMessage(Component.text("/chairstairs reload", NamedTextColor.YELLOW).append(Component.text(" - Recharger la configuration", NamedTextColor.WHITE)));
    }

    private String formatLocation(Location loc) {
        return String.format("X:%d Y:%d Z:%d (%s)", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
    }

}