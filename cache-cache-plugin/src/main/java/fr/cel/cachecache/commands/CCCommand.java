package fr.cel.cachecache.commands;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.arena.TemporaryHub;
import fr.cel.cachecache.arena.state.pregame.PreGameArenaState;
import fr.cel.cachecache.arena.state.pregame.StartingArenaState;
import fr.cel.cachecache.manager.CCArenaManager;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.manager.GroundItem;
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
    private final CCArenaManager arenaManager;

    public CCCommand(GameManager gameManager) {
        super("cachecache:cachecache", false, true);
        this.gameManager = gameManager;
        this.arenaManager = gameManager.getMain().getCcArenaManager();
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des maps Cache-Cache ont été rechargés.");
            gameManager.reloadArenaManager();
            return;
        }

        if (args[0].equalsIgnoreCase("reloadTemporary")) {
            sender.sendMessage(gameManager.getPrefix() + "Les fichiers de configuration des maps temporaires Cache-Cache ont été rechargés.");
            gameManager.reloadTemporaryHub();
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (arenaManager.getArenas().isEmpty()) {
                sender.sendMessage(gameManager.getPrefix() + "Aucune carte a été installée.");
                return;
            }

            arenaManager.getArenas().values().forEach(arena ->
                    sender.sendMessage(gameManager.getPrefix() + "Carte " + arena.getDisplayName() + " | " + arena.getArenaState().getClass().getSimpleName())
            );
            return;
        }

        if (args[0].equalsIgnoreCase("enableTemporary")) {
            TemporaryHub temporaryHub = arenaManager.getTemporaryHub();
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

        if (args[0].equalsIgnoreCase("tempHub")) {
            final TemporaryHub temporaryHub = arenaManager.getTemporaryHub();

            if (!temporaryHub.isPlayerInTempHub(player)) {
                player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans le Hub Temporaire.");
                return;
            }

            temporaryHub.chooseMapAndSendPlayers(player);
            return;
        }

        if (!arenaManager.isPlayerInArena(player)) {
            player.sendMessage(gameManager.getPrefix() + "Vous n'êtes pas dans une carte.");
            return;
        }

        final CCArena arena = arenaManager.getArenaByPlayer(player);

        if (args[0].equalsIgnoreCase("calcul")) {
            calculRedstoneLamps(player, args);
            return;
        }

        if (args[0].equalsIgnoreCase("owner")) {
            sender.sendMessage(gameManager.getPrefix() + "Le \"créateur\" de la partie est " + Objects.requireNonNull(Bukkit.getPlayer(arena.getOwner())).getName());
            return;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (arena.getArenaState() instanceof PreGameArenaState) {
                if (arena.getHunterMode() == CCArena.HunterMode.TwoHuntersAtStart) {
                    if (arena.getPlayers().size() <= 2) {
                        player.sendMessage(gameManager.getPrefix() + "Il n'y a pas assez de joueurs (minimum 3 joueurs) !");
                    } else {
                        arena.setArenaState(new StartingArenaState(arena));
                    }
                }

                else {
                    if (arena.getPlayers().size() < 2) {
                        player.sendMessage(gameManager.getPrefix() + "Il n'y a pas assez de joueurs (minimum 2 joueurs) !");
                    } else {
                        arena.setArenaState(new StartingArenaState(arena));
                    }
                }

            }
            else {
                player.sendMessage(gameManager.getPrefix() + "La partie est déjà lancée.");
            }
        }

        if (args[0].equalsIgnoreCase("listplayer")) {
            List<String> playersName = new ArrayList<>();
            arena.getPlayers().forEach(pls -> playersName.add(Objects.requireNonNull(Bukkit.getPlayer(pls)).getName()));
            player.sendMessage(gameManager.getPrefix() + "Joueurs : " + playersName);
        }

        if (args[0].equalsIgnoreCase("groundItems")) {
            List<GroundItem> groundItems = arenaManager.getArenaByPlayer(player).getAvailableGroundItems();

            StringBuilder messageBuilder = new StringBuilder(gameManager.getPrefix() + "Les Items disponibles sont :\n");
            groundItems.forEach(groundItem -> messageBuilder.append(groundItem.getDisplayName()).append("\n"));

            player.sendMessage(messageBuilder.toString());
        }

    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("start", "temphub", "enabletemporary", "list", "listplayer", "reload", "reloadtemporary", "grounditems", "owner");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            return new ArrayList<>(arenaManager.getArenas().keySet());
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