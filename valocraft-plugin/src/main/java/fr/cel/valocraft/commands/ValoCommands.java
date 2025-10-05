package fr.cel.valocraft.commands;

import fr.cel.gameapi.command.AbstractCommand;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.game.PlayingArenaState;
import fr.cel.valocraft.arena.state.pregame.PreGameArenaState;
import fr.cel.valocraft.manager.GameManager;
import fr.cel.valocraft.manager.ValoArenaManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ValoCommands extends AbstractCommand {

    private final GameManager gameManager;
    private final ValoArenaManager arenaManager;

    public ValoCommands(GameManager gameManager) {
        super("valocraft:valocraft", false, true);
        this.gameManager = gameManager;
        this.arenaManager = gameManager.getMain().getValoArenaManager();
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Les fichiers de configuration des arènes ValoCraft ont été rechargées.")));
            gameManager.reloadArenaManager();
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (arenaManager.getArenas().isEmpty()) {
                sender.sendMessage(gameManager.getPrefix().append(Component.text("Aucune arène a été installée.")));
                return;
            }

            for (ValoArena map : arenaManager.getArenas().values()) {
                Component message = gameManager.getPrefix().append(Component.text("Carte " + map.getDisplayName() + " | " + map.getArenaState().getClass().getSimpleName()));

                if (map.getArenaState() instanceof PlayingArenaState) {
                    message = message.append(Component.text(" | " + map.getRedTeam().getRoundWin() + "-" + map.getBlueTeam().getRoundWin()));
                }

                sender.sendMessage(message);
            }
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Vous devez etre un joueur pour effectuer cette commande.")));
            return;
        }

        if (!arenaManager.isPlayerInArena(player)) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Vous n'êtes pas dans une carte.")));
            return;
        }

        final ValoArena arena = arenaManager.getArenaByPlayer(player);

        if (args[0].equalsIgnoreCase("start")) {
            if (arena == null) {
                player.sendMessage(gameManager.getPrefix().append(Component.text("Vous n'êtes pas dans une arène.")));
                return;
            }

            if (arena.startGame()) {
                player.sendMessage(gameManager.getPrefix().append(Component.text("La partie a été lancée !")));
            } else {
                player.sendMessage(gameManager.getPrefix().append(Component.text("La partie ne peut pas être lancée...")));
            }
        }

        else if (args[0].equalsIgnoreCase("listplayer")) {
            List<String> players = getPlayerNames(arena.getPlayers());
            List<String> blueTeam = getPlayerNames(arena.getBlueTeam().getPlayers());
            List<String> redTeam = getPlayerNames(arena.getRedTeam().getPlayers());
            List<String> spectators = getPlayerNames(arena.getSpectators());

            player.sendMessage(gameManager.getPrefix()
                    .append(Component.text("Joueurs : " + players))
                    .appendNewline()
                    .append(Component.text("Équipe Bleue : " + blueTeam))
                    .appendNewline()
                    .append(Component.text("Équipe Rouge : " + redTeam))
                    .appendNewline()
                    .append(Component.text("Spectateurs : " + spectators))
            );
        }

        else if (args[0].equalsIgnoreCase("setround")) {
            if (arena.getArenaState() instanceof PreGameArenaState) {
                player.sendMessage(gameManager.getPrefix().append(Component.text("La partie n'est pas lancée.")));
                return;
            }

            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("blue")) {
                    arena.getBlueTeam().setRoundWin(Integer.parseInt(args[2]));
                } else if (args[1].equalsIgnoreCase("red")) {
                    arena.getRedTeam().setRoundWin(Integer.parseInt(args[2]));
                } else {
                    player.sendMessage(gameManager.getPrefix().append(Component.text("La commande est : /valocraft setround <blue/red> <nombre> (sachant que si vous mettez un nombre supérieur ou égal à 13, cela finit la partie)")));
                }
            } else {
                player.sendMessage(gameManager.getPrefix().append(Component.text("La commande est : /valocraft setround <blue/red> <nombre> (sachant que si vous mettez un nombre supérieur ou égal à 13, cela finit la partie)")));
            }
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("start", "list", "listplayer", "reload", "setround");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("setround")) {
            return List.of("blue", "red");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("setround")) {
            return IntStream.range(1, 10).mapToObj(Integer::toString).collect(Collectors.toList());
        }

        return null;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text(" "));
        sender.sendMessage(Component.text("[Aide pour les commandes du Valocraft]", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/valo start : Commence la partie dans laquelle vous êtes"));
        sender.sendMessage(Component.text("/valo list : Envoie la liste des maps avec l'état du jeu actuel"));
        sender.sendMessage(Component.text("/valo listplayer : Envoie la liste des joueurs dans la partie où vous êtes"));
        sender.sendMessage(Component.text("/valo reload : Recharge la configuration (les maps)"));
        sender.sendMessage(Component.text("/valo setround <blue/red> <number> : Permet de changer le nombre de manches gagnées pour l'équipe choisie (sachant que si vous mettez un nombre supérieur ou égal à 13, cela finit la partie)"));
    }

    private List<String> getPlayerNames(List<UUID> playerUUIDs) {
        return playerUUIDs.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).map(Player::getName).collect(Collectors.toList());
    }

    private List<String> getPlayerNames(Set<UUID> playerUUIDs) {
        return playerUUIDs.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).map(Player::getName).collect(Collectors.toList());
    }

    private void calculateInvisibleBlocks(Player player, String[] args) {
        // TODO
//        if (args.length != 8) {
//            player.sendMessage(gameManager.getPrefix() + "Usage : /cc calcul <mapName> <x1> <y1> <z1> <x2> <y2> <z2>");
//            return;
//        }
//
//        String mapName = args[1];
//        ValoArena arena = gameManager.getValoArenaManager().getArenas().get(mapName);
//
//        if (arena == null) {
//            player.sendMessage(gameManager.getPrefix() + "Rentrer un nom d'arène valide.");
//            return;
//        }
//
//        int x1 = Integer.parseInt(args[2]);
//        int y1 = Integer.parseInt(args[3]);
//        int z1 = Integer.parseInt(args[4]);
//        int x2 = Integer.parseInt(args[5]);
//        int y2 = Integer.parseInt(args[6]);
//        int z2 = Integer.parseInt(args[7]);
//
//        // Assurer que x1 <= x2, y1 <= y2, z1 <= z2
//        int minX = Math.min(x1, x2);
//        int maxX = Math.max(x1, x2);
//        int minY = Math.min(y1, y2);
//        int maxY = Math.max(y1, y2);
//        int minZ = Math.min(z1, z2);
//        int maxZ = Math.max(z1, z2);
//
//        World world = player.getWorld();
//
//        int count = 0;
//        for (int x = minX; x <= maxX; x++) {
//            for (int y = minY; y <= maxY; y++) {
//                for (int z = minZ; z <= maxZ; z++) {
//                    Block block = world.getBlockAt(x, y, z);
//                    if (block.getType() == Material.STRUCTURE_VOID) {
//                        arena.getConfig().setValue("invisible." + count, lampData);
//                        count++;
//                    }
//                }
//            }
//        }
//
//        try {
//            gameManager.getLampsConfig().save(gameManager.getLampsFile());
//            player.sendMessage("§a" + count + " lampes de redstone trouvées et sauvegardées dans lamps.yml !");
//        } catch (IOException e) {
//            player.sendMessage("§cErreur lors de la sauvegarde du fichier lamps.yml !");
//            e.printStackTrace();
//        }
    }

}