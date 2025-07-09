package fr.cel.cachecache.commands;

import fr.cel.cachecache.manager.CCMapManager;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.TemporaryHub;
import fr.cel.cachecache.map.state.game.PlayingMapState;
import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.gameapi.utils.ChatUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class CCCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final CCMapManager mapManager;

    public CCCommand(GameManager gameManager) {
        super("cachecache:cachecache", false, true);
        this.gameManager = gameManager;
        this.mapManager = gameManager.getMain().getCcMapManager();
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des maps Cache-Cache ont été rechargés.");
            gameManager.reloadMapManager();
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (mapManager.getMaps().isEmpty()) {
                sender.sendMessage(gameManager.getPrefix() + "Aucune carte a été installée.");
                return;
            }

            for (CCMap map : mapManager.getMaps().values()) {
                String message = gameManager.getPrefix() + "Carte " + map.getDisplayName() + " | " + map.getMapState().getName();

                if (map.getMapState() instanceof PlayingMapState) {
                    message += " | Timer: " + map.getTimer();
                }

                sender.sendMessage(message);
            }
            return;
        }

        // TODO /cc list <map> -> donne les informations sur la map -> nom, timer, nb de joueurs, nb de cacheurs/chercheurs
        // TODO /cc information <map> -> donne les infos de la config sur la map

        if (args[0].equalsIgnoreCase("reloadTemporary")) {
            sender.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des maps temporaires Cache-Cache ont été rechargés.");
            gameManager.reloadTemporaryHub();
            return;
        }

        if (args[0].equalsIgnoreCase("enableTemporary")) {
            if (mapManager.getTemporaryHub().hasNoMaps()) {
                sender.sendMessage(gameManager.getPrefix() + "Aucune carte a été installée.");
                return;
            }

            TemporaryHub temporaryHub = mapManager.getTemporaryHub();
            temporaryHub.setActivated(!temporaryHub.isActivated());

            if (temporaryHub.isActivated()) {
                sender.sendMessage(gameManager.getPrefix() + "Le mode de jeu temporaire du Cache-Cache est activé !");
            } else {
                sender.sendMessage(gameManager.getPrefix() + "Le mode de jeu temporaire du Cache-Cache est désactivé !");
            }
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(gameManager.getPrefix() + "Vous devez etre un joueur pour effectuer cette commande.");
            return;
        }

        if (args[0].equalsIgnoreCase("join")) {
            if (mapManager.getMaps().containsKey(args[1])) {
                if (args.length == 3) {
                    mapManager.getMaps().get(args[1]).addPlayer(Bukkit.getPlayer(args[2]), false);
                } else {
                    mapManager.getMaps().get(args[1]).addPlayer(player, false);
                }
            } else {
                sendMessageWithPrefix(player, "Merci de sélectionner une carte valide.");
            }
            return;
        }

        if (args[0].equalsIgnoreCase("tempHub")) {
            final TemporaryHub temporaryHub = mapManager.getTemporaryHub();

            if (!temporaryHub.isPlayerInTempHub(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans le Hub Temporaire.");
                return;
            }

            temporaryHub.chooseMapAndSendPlayers(player);
            return;
        }

        if (!mapManager.isPlayerInMap(player)) {
            player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une carte.");
            return;
        }

        CCMap map = mapManager.getMapByPlayer(player);

        if (args[0].equalsIgnoreCase("calcul")) {
            calculRedstoneLamps(player, args);
        }

        if (args[0].equalsIgnoreCase("owner")) {
            sender.sendMessage(gameManager.getPrefix() + "Le \"créateur\" de la partie est " + Bukkit.getPlayer(map.getOwner()).getName());
        }

        if (args[0].equalsIgnoreCase("start")) {
            map.startGame(player);
        }

        if (args[0].equalsIgnoreCase("listplayer")) {
            StringBuilder sb = new StringBuilder();

            for (UUID uuidPlayer : map.getPlayers()) {
                Player pl = Bukkit.getPlayer(uuidPlayer);
                if (pl != null) sb.append(pl.getName()).append(", ");
            }

            player.sendMessage(gameManager.getPrefix() + "Joueurs : " + sb);
        }

        if (args[0].equalsIgnoreCase("grounditems")) {
            StringBuilder messageBuilder = new StringBuilder(gameManager.getPrefix() + "Les Items disponibles sont :\n");
            mapManager.getMapByPlayer(player).getAvailableGroundItems().forEach(groundItem ->
                    messageBuilder.append(groundItem.getDisplayName()).append("\n"));

            player.sendMessage(messageBuilder.toString());
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("start", "temphub", "enabletemporary", "list", "listplayer", "reload", "reloadtemporary", "grounditems", "owner", "join", "calcul");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            return new ArrayList<>(mapManager.getMaps().keySet());
        }

        return null;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ChatUtility.format("[Aide pour les commandes du Cache-Cache]", ChatUtility.GOLD));
        sender.sendMessage("/cc start : Commence la partie dans laquelle vous êtes");
        sender.sendMessage("/cc temphub : Commence la partie du mode temporaire");
        sender.sendMessage("/cc enableTemporary : Active/Désactive le mode temporaire");
        sender.sendMessage("/cc list : Envoie la liste des maps avec l'état du jeu actuel");
        sender.sendMessage("/cc listplayer : Envoie la liste des joueurs dans la partie où vous êtes");
        sender.sendMessage("/cc reload : Recharge les maps");
        sender.sendMessage("/cc reloadTemporary : Recharge les maps temporaires");
        sender.sendMessage("/cc groundItems : Envoie la liste des Items disponibles dans la map où vous êtes");
        sender.sendMessage("/cc owner : Envoie le nom du \"créateur\" de la partie");
        sender.sendMessage("/cc calcul x1 y1 z1 x2 y2 z2 : (Re-)Calcule les lampes de redstone (normalement) de la map Bunker.");
    }

    private void calculRedstoneLamps(Player player, String[] args) {
        if (args.length != 7) {
            sendMessageWithPrefix(player, "Usage : /cc calcul x1 y1 z1 x2 y2 z2");
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
                    if (block.getType() == Material.REDSTONE_LAMP) {
                        Map<String, Object> lampData = new HashMap<>();
                        lampData.put("x", x);
                        lampData.put("y", y);
                        lampData.put("z", z);

                        gameManager.getLampsConfig().set("lamps." + count, lampData);
                        count++;
                    }
                }
            }
        }

        try {
            gameManager.getLampsConfig().save(gameManager.getLampsFile());
            player.sendMessage("§a" + count + " lampes de redstone trouvées et sauvegardées dans lamps.yml !");
        } catch (IOException e) {
            player.sendMessage("§cErreur lors de la sauvegarde du fichier lamps.yml !");
            e.printStackTrace();
        }
    }

}