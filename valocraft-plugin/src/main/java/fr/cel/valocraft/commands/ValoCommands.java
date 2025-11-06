package fr.cel.valocraft.commands;

import fr.cel.gameapi.manager.command.AbstractCommand;
import fr.cel.valocraft.arena.ValoArena;
import fr.cel.valocraft.arena.state.game.PlayingArenaState;
import fr.cel.valocraft.arena.state.pregame.PreGameArenaState;
import fr.cel.valocraft.arena.state.pregame.StartingArenaState;
import fr.cel.valocraft.manager.GameManager;
import fr.cel.valocraft.manager.ValoArenaManager;
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
            sender.sendMessage(gameManager.getPrefix().append(Component.text("Les fichiers de configuration des arènes Valocraft ont été rechargées.")));
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

        final ValoArena arena = arenaManager.getArenaByPlayer(player);
        if (arena == null) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Vous n'êtes pas dans une carte.")));
            return;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (arena.startGame()) {
                player.sendMessage(gameManager.getPrefix().append(Component.text("La partie a été lancée !")));
            } else {
                player.sendMessage(gameManager.getPrefix().append(Component.text("La partie ne peut pas être lancée...")));
            }
        }

        else if (args[0].equalsIgnoreCase("listplayer")) {
            List<String> blueTeam = getPlayerNames(arena.getBlueTeam().getPlayers());
            List<String> redTeam = getPlayerNames(arena.getRedTeam().getPlayers());
            List<String> spectators = getPlayerNames(arena.getSpectators());

            player.sendMessage(gameManager.getPrefix()
                    .append(Component.text("Équipe Bleue : " + blueTeam))
                    .appendNewline()
                    .append(Component.text("Équipe Rouge : " + redTeam))
                    .appendNewline()
                    .append(Component.text("Spectateurs : " + spectators))
            );
        }

        else if (args[0].equalsIgnoreCase("setround")) {
            if (arena.getArenaState() instanceof PreGameArenaState || arena.getArenaState() instanceof StartingArenaState) {
                player.sendMessage(gameManager.getPrefix().append(Component.text("La partie n'est pas lancée.")));
                return;
            }

            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("blue")) {
                    int round;
                    try {
                        round = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(gameManager.getPrefix().append(Component.text("Le nombre de manches doit être un nombre valide.")));
                        return;
                    }

                    arena.getBlueTeam().setRoundWin(round);
                }

                else if (args[1].equalsIgnoreCase("red")) {
                    int round;
                    try {
                        round = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(gameManager.getPrefix().append(Component.text("Le nombre de manches doit être un nombre valide.")));
                        return;
                    }

                    arena.getRedTeam().setRoundWin(round);
                }

                else {
                    player.sendMessage(gameManager.getPrefix().append(Component.text("Usage: /valocraft setround <blue/red> <number> (sachant que si vous mettez un nombre supérieur ou égal à 13, cela finit la partie)")));
                }
            } else {
                player.sendMessage(gameManager.getPrefix().append(Component.text("Usage: /valocraft setround <blue/red> <number> (sachant que si vous mettez un nombre supérieur ou égal à 13, cela finit la partie)")));
            }
        }

        else if (args[0].equalsIgnoreCase("calcul")) {
            calculateInvisibleBlocks(player, args);
        }

        else if (args[0].equalsIgnoreCase("barriers")) {
            if (args.length != 2) {
                player.sendMessage(gameManager.getPrefix().append(Component.text("Usage: /valo barriers <show/hide>")));
                return;
            }

            if (args[1].equalsIgnoreCase("show")) {
                arena.showInvisibleBarriers();
                player.sendMessage(gameManager.getPrefix().append(Component.text("Les barrières invisibles ont été affichées.")));
            }

            else if (args[1].equalsIgnoreCase("hide")) {
                arena.hideInvisibleBarriers();
                player.sendMessage(gameManager.getPrefix().append(Component.text("Les barrières invisibles ont été cachées.")));
            }

            else {
                player.sendMessage(gameManager.getPrefix().append(Component.text("Usage : /valo barriers <show/hide>")));
            }
        }

        else {
            sendHelp(sender);
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("start", "list", "listplayer", "reload", "setround", "calcul", "barriers");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("setround")) {
            return List.of("blue", "red");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("setround")) {
            return IntStream.range(1, 10).mapToObj(Integer::toString).collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("barriers")) {
            return List.of("show", "hide");
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
        if (args.length != 8) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("Usage : /cc calcul <mapName> <x1> <y1> <z1> <x2> <y2> <z2>")));
            return;
        }

        ValoArena arena = gameManager.getMain().getValoArenaManager().getArenas().get(args[1].toLowerCase());

        if (arena == null) {
            player.sendMessage(gameManager.getPrefix().append(Component.text("La map '" + args[1] + "' n'existe pas.", NamedTextColor.RED)));
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
            player.sendMessage(gameManager.getPrefix().append(Component.text("Les coordonnées doivent être des nombres valides.", NamedTextColor.RED)));
            return;
        }

        // Assurer que x1 <= x2, y1 <= y2, z1 <= z2
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        World world = player.getWorld();
        List<String> invisibleBlocks = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.STRUCTURE_VOID) {
                        invisibleBlocks.add(world.getName() + "," + x + "," + y + "," + z);
                    }
                }
            }
        }

        arena.getArenaConfig().setValue("invisibleblocks", invisibleBlocks);
        arena.setInvisibleBarriersLocations(arena.getArenaConfig().getLocationInvisibleBarrier());

        player.sendMessage(Component.text(invisibleBlocks.size() + " barrières invisibles trouvées et sauvegardées dans le fichier de configuration de la carte !", NamedTextColor.GREEN));
    }

}