package fr.cel.cachecache.commands;

import fr.cel.cachecache.manager.CCMapManager;
import fr.cel.cachecache.manager.GameManager;
import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.map.CCMap;
import fr.cel.cachecache.map.TemporaryHub;
import fr.cel.cachecache.map.state.game.PlayingMapState;
import fr.cel.gameapi.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CCCommand extends AbstractCommand {

    private final GameManager gameManager;
    private final CCMapManager mapManager;

    public CCCommand(GameManager gameManager) {
        super("cachecache:cachecache", false, true);
        this.gameManager = gameManager;
        this.mapManager = gameManager.getMain().getCcMapManager();
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Les fichiers de configuration des maps Cache-Cache ont été rechargés.")));
            gameManager.reloadMapManager();
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (mapManager.getMaps().isEmpty()) {
                sender.sendMessage(gameManager.getPrefix().append(Component.text("Aucune carte a été installée.")));
                return;
            }

            if (args.length == 2) {
                if (mapManager.getMaps().containsKey(args[1])) {
                    CCMap map = mapManager.getMaps().get(args[1]);

                    if (map.getMapState() instanceof PlayingMapState) {
                        sender.sendMessage(gameManager.getPrefix()
                                .append(Component.text("Carte " + map.getDisplayName()))
                                .appendNewline()
                                .append(Component.text("Temps : " + getTimerString(map)))
                                .appendNewline()
                                .append(Component.text("Hôte : " + Objects.requireNonNull(Bukkit.getPlayer(map.getOwner())).getName()))
                                .appendNewline()
                                .append(Component.text("Nombre de joueurs : " + map.getPlayers().size()))
                                .appendNewline()
                                .append(Component.text("Nombre de cacheurs : " + map.getHiders().size()))
                                .appendNewline()
                                .append(Component.text("Nombre de chercheurs : " + map.getSeekers().size()))
                        );
                    }

                    else {
                        sender.sendMessage(gameManager.getPrefix().append(Component.text("Aucune partie n'est en cours sur cette carte.")));
                    }
                } else {
                    sender.sendMessage(gameManager.getPrefix().append(Component.text("Merci de mettre une carte valide.")));
                }

                return;
            }

            else {
                for (CCMap map : mapManager.getMaps().values()) {
                    Component message = gameManager.getPrefix().append(Component.text("Carte " + map.getDisplayName() + " | " + map.getMapState().getName()));

                    if (map.getMapState() instanceof PlayingMapState) {
                        message = message.append(Component.text(" | Timer : " + map.getTimer()));
                    }

                    sender.sendMessage(message);
                }
            }

            return;
        }

        if (args[0].equalsIgnoreCase("information")) {
            if (args.length == 2) {
                if (mapManager.getMaps().containsKey(args[1])) {
                    CCMap map = mapManager.getMaps().get(args[1]);

                    String availableGroundItems = map.getAvailableGroundItems().stream().map(GroundItem::getName).collect(Collectors.joining(", "));
                    String locationGroundItems = map.getLocationGroundItems().stream().map(this::locToString).collect(Collectors.joining(", "));

                    sender.sendMessage(gameManager.getPrefix()
                            .append(Component.text("Map " + map.getDisplayName() + " (" + map.getMapName() + ")"))
                            .appendNewline()
                            .append(Component.text("Mode : " + map.getCcMode().getName()))
                            .appendNewline()
                            .append(Component.text("Spawn Loc : " + locToString(map.getSpawnLoc())))
                            .appendNewline()
                            .append(Component.text("Waiting Loc : " + locToString(map.getWaitingLoc())))
                            .appendNewline()
                            .append(Component.text("Fall Damage : " + map.isFallDamage()))
                            .appendNewline()
                            .append(Component.text("Best Player : " + map.getBestPlayer() + " - Best Timer : " + map.getBestTimer()))
                            .appendNewline()
                            .append(Component.text("Available Ground Items : " + availableGroundItems))
                            .appendNewline()
                            .append(Component.text("Location Ground Items : " + locationGroundItems))
                    );

                } else {
                    sender.sendMessage(gameManager.getPrefix().append(Component.text("Merci de mettre une carte valide.")));
                }
            } else {
                sender.sendMessage(gameManager.getPrefix().append(Component.text("Merci d'indiquer une carte.")));
            }

            return;
        }

        if (args[0].equalsIgnoreCase("reloadTemporary")) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Les fichiers de configuration des maps temporaires Cache-Cache ont été rechargés.")));
            gameManager.reloadTemporaryHub();
            return;
        }

        if (args[0].equalsIgnoreCase("enableTemporary")) {
            if (mapManager.getTemporaryHub().hasNoMaps()) {
                sender.sendMessage(gameManager.getPrefix().append(Component.text("Aucune carte pour le mode de jeu temporaire du Cache-Cache a été installée.")));
                return;
            }

            TemporaryHub temporaryHub = mapManager.getTemporaryHub();
            temporaryHub.setActivated(!temporaryHub.isActivated());

            if (temporaryHub.isActivated()) {
                sender.sendMessage(gameManager.getPrefix().append(Component.text("Le mode de jeu temporaire du Cache-Cache est activé !")));
            } else {
                sender.sendMessage(gameManager.getPrefix().append(Component.text("Le mode de jeu temporaire du Cache-Cache est désactivé !")));
            }
            return;
        }

        if (args[0].equalsIgnoreCase("join")) {
            if (mapManager.getMaps().containsKey(args[1])) {
                if (args.length == 3) {
                    Player target = Bukkit.getPlayerExact(args[2]);
                    if (target == null) {
                        sender.sendMessage(gameManager.getPrefix().append(Component.text("Ce joueur est hors-ligne ou n'existe pas...")));
                        return;
                    }

                    mapManager.getMaps().get(args[1]).addPlayer(target, false);
                } else if (sender instanceof Player player && args.length == 2) {
                    mapManager.getMaps().get(args[1]).addPlayer(player, false);
                } else {
                    sender.sendMessage(Component.text("Usage: /cc join <map> <player>"));
                }
            } else {
                sender.sendMessage(gameManager.getPrefix().append(Component.text("Merci de sélectionner une carte valide.")));
            }
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Vous devez etre un joueur pour effectuer cette commande.")));
            return;
        }

        if (args[0].equalsIgnoreCase("tempHub")) {
            final TemporaryHub temporaryHub = mapManager.getTemporaryHub();

            if (!temporaryHub.isPlayerInTempHub(player)) {
                player.sendMessage(gameManager.getPrefix().append(Component.text("Vous n'êtes pas dans le Hub Temporaire.")));
                return;
            }

            temporaryHub.chooseMapAndSendPlayers(player);
            return;
        }

        if (args[0].equalsIgnoreCase("calcul")) {
            calculRedstoneLamps(player, args);
            return;
        }

        if (!mapManager.isPlayerInMap(player)) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Vous n'êtes pas dans une carte.")));
            return;
        }

        CCMap map = mapManager.getMapByPlayer(player);

        if (args[0].equalsIgnoreCase("start")) {
            map.startGame(player);
        }

        if (args[0].equalsIgnoreCase("listplayer")) {
            Component message = gameManager.getPrefix().append(Component.text("Joueurs : "));

            for (UUID uuidPlayer : map.getPlayers()) {
                Player pl = Bukkit.getPlayer(uuidPlayer);
                if (pl != null) message = message.append(Component.text(pl.getName() + ", "));
            }

            player.sendMessage(message);
        }

        if (args[0].equalsIgnoreCase("grounditems")) {
            Component message = gameManager.getPrefix().append(Component.text("Les Items disponibles sont :").appendNewline());

            for (GroundItem groundItem : mapManager.getMapByPlayer(player).getAvailableGroundItems())
                message = message.append(groundItem.getItemName()).appendNewline();

            player.sendMessage(message);
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
        sender.sendMessage(Component.text(" "));
        sender.sendMessage(Component.text("[Aide pour les commandes du Cache-Cache]", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/cc start : Commence la partie dans laquelle vous êtes"));
        sender.sendMessage(Component.text("/cc temphub : Commence la partie du mode temporaire"));
        sender.sendMessage(Component.text("/cc enableTemporary : Active/Désactive le mode temporaire"));
        sender.sendMessage(Component.text("/cc list : Envoie la liste des maps avec l'état du jeu actuel"));
        sender.sendMessage(Component.text("/cc listplayer : Envoie la liste des joueurs dans la partie où vous êtes"));
        sender.sendMessage(Component.text("/cc reload : Recharge les maps"));
        sender.sendMessage(Component.text("/cc reloadTemporary : Recharge les maps temporaires"));
        sender.sendMessage(Component.text("/cc groundItems : Envoie la liste des Items disponibles dans la map où vous êtes"));
        sender.sendMessage(Component.text("/cc owner : Envoie le nom du gérant de la partie"));
        sender.sendMessage(Component.text("/cc calcul x1 y1 z1 x2 y2 z2 : (Re-)Calcule les lampes de redstone (normalement) de la map Bunker."));
    }

    private void calculRedstoneLamps(Player player, String[] args) {
        if (args.length != 7) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Usage : /cc calcul x1 y1 z1 x2 y2 z2")));
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
            player.sendMessage(Component.text(count + " lampes de redstone trouvées et sauvegardées dans lamps.yml !", NamedTextColor.GREEN));
        } catch (IOException e) {
            player.sendMessage(Component.text("Erreur lors de la sauvegarde du fichier lamps.yml !", NamedTextColor.RED));
            gameManager.getMain().getLogger().severe("Erreur dans la sauvegarde du fichier des lampes : " + e.getMessage());
        }
    }

    private String getTimerString(CCMap map) {
        int hours = map.getTimer() / 3600;
        int minutes = (map.getTimer() % 3600) / 60;
        int seconds = map.getTimer() % 60;

        String timerString;

        if (minutes == 0 && hours == 0) {
            if (seconds < 10) {
                if (seconds == 0 || seconds == 1) {
                    timerString = String.format("%01d seconde", seconds);
                } else {
                    timerString = String.format("%01d secondes", seconds);
                }
            } else {
                timerString = String.format("%02d secondes", seconds);
            }
        }
        else if (hours == 0) {
            timerString = String.format("%02dmin%02ds", minutes, seconds);
        }
        else {
            timerString = String.format("%02dh%02dmin%02ds", hours, minutes, seconds);
        }

        return timerString;
    }

    private String locToString(Location loc) {
        return "(" + loc.x() + ", " + loc.y() + ", " + loc.z() + ")";
    }

}