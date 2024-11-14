package fr.cel.halloween.commands;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.gameapi.utils.ChatUtility;
import fr.cel.halloween.manager.GameManager;
import fr.cel.halloween.manager.HalloweenMapManager;
import fr.cel.halloween.map.HalloweenMap;
import fr.cel.halloween.map.state.pregame.PreGameMapState;
import fr.cel.halloween.map.state.pregame.StartingMapState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
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
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(GameManager.getPrefix() + "Les fichiers de configuration des cartes Halloween ont été rechargés.");
            gameManager.reloadMapManager();
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (mapManager.getMaps().isEmpty()) {
                sender.sendMessage(GameManager.getPrefix() + "Aucune carte a été installée.");
                return;
            }

            mapManager.getMaps().values().forEach(arena ->
                    sender.sendMessage(GameManager.getPrefix() + "Carte " + arena.getDisplayName() + " | " + arena.getMapState().getClass().getSimpleName())
            );
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(GameManager.getPrefix() + "Vous devez etre un joueur pour effectuer cette commande.");
            return;
        }

        if (!mapManager.isPlayerInMap(player)) {
            player.sendMessage(GameManager.getPrefix() + "Vous n'êtes pas dans une carte.");
            return;
        }

        final HalloweenMap map = mapManager.getMapByPlayer(player);

        if (args[0].equalsIgnoreCase("start")) {
            if (map.getMapState() instanceof PreGameMapState) {
                if (map.getPlayers().size() < 2) {
                    player.sendMessage(GameManager.getPrefix() + "Il n'y a pas assez de joueurs (minimum 2 joueurs) !");
                } else {
                    map.setMapState(new StartingMapState(map));
                }
            }
            else {
                player.sendMessage(GameManager.getPrefix() + "La partie est déjà lancée.");
            }
        }

        if (args[0].equalsIgnoreCase("listplayer")) {
            List<String> playersName = new ArrayList<>();
            map.getPlayers().forEach(pls -> playersName.add(Objects.requireNonNull(Bukkit.getPlayer(pls)).getName()));
            player.sendMessage(GameManager.getPrefix() + "Joueurs : " + playersName);
        }

        if (args[0].equalsIgnoreCase("spawnsoul")) {
            calculSpawnSouls(player, args, map);
            return;
        }

        if (args[0].equalsIgnoreCase("spawnplayer")) {
            calculSpawnPlayer(player, args, map);
        }

    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("reload", "list", "start", "listplayer", "spawnsoul", "spawnplayer");
        }
        return null;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ChatUtility.format("[Aide pour les commandes de l'événement Halloween]", ChatUtility.GOLD));
        sender.sendMessage("/halloween reload : Recharge les maps");
        sender.sendMessage("/halloween list : Envoie la liste des maps avec l'état du jeu actuel");
        sender.sendMessage("/halloween start : Commence la partie dans laquelle vous êtes");
        sender.sendMessage("/halloween listplayer : Envoie la liste des joueurs dans la partie où vous êtes");
        sender.sendMessage("/halloween spawnsoul x1 y1 z1 x2 y2 z2 : (Re-)Calcule les emplacements de spawn.");
        sender.sendMessage("/halloween spawnplayer x1 y1 z1 x2 y2 z2 : (Re-)Calcule les emplacements de réapparition des joueurs.");
    }

    private void calculSpawnSouls(Player player, String[] args, HalloweenMap map) {
        if (args.length != 7) {
            sendMessageWithPrefix(player, "Usage : /halloween calcul x1 y1 z1 x2 y2 z2");
            return;
        }

        int x1 = Integer.parseInt(args[1]);
        int y1 = Integer.parseInt(args[2]);
        int z1 = Integer.parseInt(args[3]);
        int x2 = Integer.parseInt(args[4]);
        int y2 = Integer.parseInt(args[5]);
        int z2 = Integer.parseInt(args[6]);

        // Assurer que x1 <= x2, y1 <= y2, z1 <= z2
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        World world = player.getWorld();

        int count = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.MAGENTA_WOOL) {
                        Map<String, Object> lampData = new HashMap<>();
                        lampData.put("x", x);
                        lampData.put("y", y);
                        lampData.put("z", z);

                        gameManager.getSoulsConfig().set("souls." + count, lampData);
                        count++;
                    }
                }
            }
        }

        try {
            gameManager.getSoulsConfig().save(gameManager.getSoulsFile());

            map.setSoulLocation();

            for (Location location : map.getSoulLocations()) {
                player.sendMessage("souls " + location.getX() + " " + location.getY() + " " + location.getZ());
            }
            player.sendMessage("§b" + map.getSoulLocations().size() + " spawns de vestiges d'âmes trouvés !");

            player.sendMessage("§a" + count + " spawns de vestiges d'âmes trouvés et sauvegardés dans souls.yml !");
        } catch (IOException e) {
            player.sendMessage("§cErreur lors de la sauvegarde du fichier souls.yml !");
            e.printStackTrace();
        }
    }

    private void calculSpawnPlayer(Player player, String[] args, HalloweenMap map) {
        if (args.length != 7) {
            sendMessageWithPrefix(player, "Usage : /halloween calculplayer x1 y1 z1 x2 y2 z2");
            return;
        }

        int x1 = Integer.parseInt(args[1]);
        int y1 = Integer.parseInt(args[2]);
        int z1 = Integer.parseInt(args[3]);
        int x2 = Integer.parseInt(args[4]);
        int y2 = Integer.parseInt(args[5]);
        int z2 = Integer.parseInt(args[6]);

        // Assurer que x1 <= x2, y1 <= y2, z1 <= z2
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        World world = player.getWorld();

        int count = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.LIME_WOOL) {
                        Map<String, Object> lampData = new HashMap<>();
                        lampData.put("x", x);
                        lampData.put("y", y);
                        lampData.put("z", z);

                        gameManager.getSoulsConfig().set("spawnplayer." + count, lampData);
                        count++;
                    }
                }
            }
        }

        try {
            gameManager.getPlayersConfig().save(gameManager.getPlayersFile());

            map.setSpawnPlayerLocation();

            for (Location location : map.getSpawnPlayerLocations()) {
                player.sendMessage("souls " + location.getX() + " " + location.getY() + " " + location.getZ());
            }
            player.sendMessage("§b" + map.getSpawnPlayerLocations().size() + " points de réapparition trouvés dans spawnplayer.yml !");

            player.sendMessage("§a" + count + " points de réapparition trouvés et sauvegardés dans spawnplayer.yml !");
        } catch (IOException e) {
            player.sendMessage("§cErreur lors de la sauvegarde du fichier spawnplayer.yml !");
            e.printStackTrace();
        }
    }

}