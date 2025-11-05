package fr.cel.halloween.commands;

import fr.cel.gameapi.manager.command.AbstractCommand;
import fr.cel.halloween.manager.GameManager;
import fr.cel.halloween.manager.HalloweenMapManager;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.state.pregame.PreGameMapState;
import fr.cel.halloween.map.state.pregame.StartingMapState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HalloweenCommands extends AbstractCommand {

    private final GameManager gameManager;
    private final HalloweenMapManager mapManager;

    public HalloweenCommands(GameManager gameManager) {
        super("halloween:halloween", false, true);
        this.gameManager = gameManager;
        this.mapManager = gameManager.getMain().getHalloweenMapManager();
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(GameManager.getPrefix().append(Component.text("Les fichiers de configuration des cartes Halloween ont été rechargés.")));
            gameManager.reloadMapManager();
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (mapManager.getMaps().isEmpty()) {
                sender.sendMessage(GameManager.getPrefix().append(Component.text("Aucune carte a été installée.")));
                return;
            }

            for (HalloweenMap m : mapManager.getMaps().values()) {
                sender.sendMessage(GameManager.getPrefix()
                        .append(Component.text("Carte " + m.getDisplayName() + " | " + m.getMapState().getName())));
            }
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(GameManager.getPrefix().append(Component.text("Vous devez etre un joueur pour effectuer cette commande.")));
            return;
        }

        if (!mapManager.isPlayerInMap(player)) {
            player.sendMessage(GameManager.getPrefix().append(Component.text("Vous n'êtes pas dans une carte.")));
            return;
        }

        final HalloweenMap map = mapManager.getMapByPlayer(player);

        if (args[0].equalsIgnoreCase("start")) {
            if (map.getMapState() instanceof PreGameMapState) {
                if (map.getPlayers().size() < 2) {
                    player.sendMessage(GameManager.getPrefix().append(Component.text("Il n'y a pas assez de joueurs (minimum 2 joueurs) !")));
                } else {
                    map.setMapState(new StartingMapState(map));
                }
            }
            else {
                player.sendMessage(GameManager.getPrefix().append(Component.text("La partie est déjà lancée.")));
            }
            return;
        }

        if (args[0].equalsIgnoreCase("listplayer")) {
            List<String> playersName = new ArrayList<>();
            map.getPlayers().forEach(pls -> playersName.add(Objects.requireNonNull(Bukkit.getPlayer(pls)).getName()));
            player.sendMessage(GameManager.getPrefix().append(Component.text("Joueurs : " + playersName)));
            return;
        }

        if (args[0].equalsIgnoreCase("spawnsoul")) {
            calculSpawnSouls(player, args, map);
            return;
        }

        if (args[0].equalsIgnoreCase("playerspawns")) {
            calculPlayerSpawns(player, args, map);
            return;
        }

        if (args[0].equalsIgnoreCase("chests")) {
            calculChests(player, args, map);
        }

    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("reload", "list", "start", "listplayer", "spawnsoul", "playerspawns", "chests");
        }
        return null;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text(" "));
        sender.sendMessage(Component.text("[Aide pour les commandes de l'événement Halloween]", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/halloween reload : Recharge les maps"));
        sender.sendMessage(Component.text("/halloween list : Envoie la liste des maps avec l'état du jeu actuel"));
        sender.sendMessage(Component.text("/halloween start : Commence la partie dans laquelle vous êtes"));
        sender.sendMessage(Component.text("/halloween listplayer : Envoie la liste des joueurs dans la partie où vous êtes"));
        sender.sendMessage(Component.text("/halloween spawnsoul x1 y1 z1 x2 y2 z2 : (Re-)Calcule les emplacements de spawn."));
        sender.sendMessage(Component.text("/halloween playerspawns x1 y1 z1 x2 y2 z2 : (Re-)Calcule les emplacements de réapparition des joueurs."));
    }

    private void calculSpawnSouls(Player player, String[] args, HalloweenMap map) {
        if (args.length != 8) {
            player.sendMessage(GameManager.getPrefix().append(Component.text("Usage : /halloween spawnsoul <mapName> <x1> <y1> <z1> <x2> <y2> <z2>")));
            return;
        }

        int x1, y1, z1, x2, y2, z2;
        try {
            x1 = Integer.parseInt(args[2]);
            y1 = Integer.parseInt(args[3]);
            z1 = Integer.parseInt(args[4]);
            x2 = Integer.parseInt(args[5]);
            y2 = Integer.parseInt(args[6]);
            z2 = Integer.parseInt(args[7]);
        } catch (NumberFormatException e) {
            player.sendMessage(GameManager.getPrefix().append(Component.text("Les coordonnées doivent être des nombres valides.", NamedTextColor.RED)));
            return;
        }

        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        World world = player.getWorld();
        List<String> soulBlocks = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.MAGENTA_WOOL) {
                        soulBlocks.add(world.getName() + "," + x + "," + y + "," + z);
                    }
                }
            }
        }

        map.getMapConfig().setValue("souls", soulBlocks);
        map.setSoulLocations(map.getMapConfig().getSoulLocations());

        player.sendMessage(Component.text(soulBlocks.size() + " blocs d'apparition pour les âmes trouvés et sauvegardés dans le fichier de configuration de la carte !", NamedTextColor.GREEN));
    }

    private void calculPlayerSpawns(Player player, String[] args, HalloweenMap map) {
        if (args.length != 8) {
            player.sendMessage(GameManager.getPrefix().append(Component.text("Usage : /halloween playerspawns <mapName> <x1> <y1> <z1> <x2> <y2> <z2>")));
            return;
        }

        int x1, y1, z1, x2, y2, z2;
        try {
            x1 = Integer.parseInt(args[2]);
            y1 = Integer.parseInt(args[3]);
            z1 = Integer.parseInt(args[4]);
            x2 = Integer.parseInt(args[5]);
            y2 = Integer.parseInt(args[6]);
            z2 = Integer.parseInt(args[7]);
        } catch (NumberFormatException e) {
            player.sendMessage(GameManager.getPrefix().append(Component.text("Les coordonnées doivent être des nombres valides.", NamedTextColor.RED)));
            return;
        }

        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        World world = player.getWorld();
        List<String> playerSpawnsBlocks = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.LIME_WOOL) {
                        playerSpawnsBlocks.add(world.getName() + "," + x + "," + y + "," + z);
                    }
                }
            }
        }

        map.getMapConfig().setValue("playerSpawns", playerSpawnsBlocks);
        map.setPlayerSpawnsLocations(map.getMapConfig().getPlayerSpawnsLocations());

        player.sendMessage(Component.text(playerSpawnsBlocks.size() + " points d'apparition pour les joueurs trouvés et sauvegardés dans le fichier de configuration de la carte !", NamedTextColor.GREEN));
    }

    private void calculChests(Player player, String @NotNull [] args, HalloweenMap map) {
        if (args.length != 8) {
            player.sendMessage(GameManager.getPrefix().append(Component.text("Usage : /halloween chests <mapName> <x1> <y1> <z1> <x2> <y2> <z2>")));
            return;
        }

        int x1, y1, z1, x2, y2, z2;
        try {
            x1 = Integer.parseInt(args[2]);
            y1 = Integer.parseInt(args[3]);
            z1 = Integer.parseInt(args[4]);
            x2 = Integer.parseInt(args[5]);
            y2 = Integer.parseInt(args[6]);
            z2 = Integer.parseInt(args[7]);
        } catch (NumberFormatException e) {
            player.sendMessage(GameManager.getPrefix().append(Component.text("Les coordonnées doivent être des nombres valides.", NamedTextColor.RED)));
            return;
        }

        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        World world = player.getWorld();
        List<String> chestBlocks = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.CHEST) {
                        chestBlocks.add(world.getName() + "," + x + "," + y + "," + z);
                    }
                }
            }
        }

        map.getMapConfig().setValue("chestLocations", chestBlocks);
        map.setChestLocations(map.getMapConfig().getChestLocations());

        player.sendMessage(Component.text(chestBlocks.size() + " coffres trouvés et sauvegardés dans le fichier de configuration de la carte !", NamedTextColor.GREEN));
    }

}