package fr.cel.gameapi.commands.winter;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import fr.cel.gameapi.manager.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WinterPointsCommand extends AbstractCommand {

    private final PlayerManager playerManager;

    public WinterPointsCommand(PlayerManager playerManager) {
        super("gameapi:winterpoints", false, true);
        this.playerManager = playerManager;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("get")) {
            if (args.length == 1) {
                if (sender instanceof Player player) {
                    sendWinterPointsMessage(player, player);
                } else {
                    sender.sendMessage(GameAPI.getPrefix().append(Component.text("Tu n'es pas un joueur...")));
                }
                return;
            }

            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (isPlayerOnline(target, sender)) {
                    sendWinterPointsMessage(sender, target);
                }
                return;
            }

            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);

                if (isPlayerOnline(target, sender)) {
                    int amount;
                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sendMessageWithPrefix(sender, Component.text("Le montant doit être un nombre valide.", NamedTextColor.RED));
                        return;
                    }

                    if (amount <= 0) {
                        sendMessageWithPrefix(sender, Component.text("Le montant doit être positif.", NamedTextColor.RED));
                        return;
                    }

                    playerManager.getPlayerData(target).getWinterPlayerData().addWinterPoints(amount);
                    sendMessageWithPrefix(sender, Component.text("Tu as donné " + amount + " point(s) d'hiver à " + target.getName(), NamedTextColor.GREEN));

                    target.sendMessage(GameAPI.getPrefix().append(Component.text("Tu as reçu " + amount + " point(s) d'hiver ! ❄", NamedTextColor.AQUA)));
                }
                return;
            }

            sendHelp(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);

                if (isPlayerOnline(target, sender)) {
                    int amount;
                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sendMessageWithPrefix(sender, Component.text("Le montant doit être un nombre valide.", NamedTextColor.RED));
                        return;
                    }

                    if (amount <= 0) {
                        sendMessageWithPrefix(sender, Component.text("Le montant doit être positif.", NamedTextColor.RED));
                        return;
                    }

                    int currentPoints = playerManager.getPlayerData(target).getWinterPlayerData().getWinterPoints();
                    if (currentPoints - amount < 0) {
                        sendMessageWithPrefix(sender, Component.text("Vous ne pouvez pas retirer autant de points à " + target.getName() + ", il/elle n'en a pas assez (actuellement: " + currentPoints + ").", NamedTextColor.RED));
                        return;
                    }

                    playerManager.getPlayerData(target).getWinterPlayerData().removeWinterPoints(amount);
                    sendMessageWithPrefix(sender, Component.text("Tu as retiré " + amount + " point(s) d'hiver à " + target.getName(), NamedTextColor.YELLOW));

                    target.sendMessage(GameAPI.getPrefix().append(Component.text("Tu as perdu " + amount + " point(s) d'hiver.", NamedTextColor.RED)));
                }
                return;
            }

            sendHelp(sender);
            return;
        }

        sendHelp(sender);
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("add", "remove", "get");
        }

        if (args.length == 2) return null;

        if (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            return IntStream.rangeClosed(1, 10)
                    .mapToObj(i -> String.valueOf(i * 10))
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text(" "));
        sender.sendMessage(Component.text("[Aide - Système de Points d'Hiver]", NamedTextColor.AQUA));
        sender.sendMessage(Component.text(" "));
        sender.sendMessage(Component.text("  /winterpoints get", NamedTextColor.WHITE)
                .append(Component.text(" - Voir tes points d'hiver", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("  /winterpoints get <pseudo>", NamedTextColor.WHITE)
                .append(Component.text(" - Voir les points d'un joueur", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text(" "));
        sender.sendMessage(Component.text("  /winterpoints add <pseudo> <montant>", NamedTextColor.WHITE)
                .append(Component.text(" - Ajouter des points", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("  /winterpoints remove <pseudo> <montant>", NamedTextColor.WHITE)
                .append(Component.text(" - Retirer des points", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text(" "));
    }

    private void sendWinterPointsMessage(CommandSender sender, Player target) {
        int points = playerManager.getPlayerData(target).getWinterPlayerData().getWinterPoints();

        Component message;
        if (sender.equals(target)) {
            message = Component.text("Tu as ", NamedTextColor.AQUA)
                    .append(Component.text(points, NamedTextColor.WHITE))
                    .append(Component.text(" point(s) d'hiver ❄", NamedTextColor.AQUA));
        } else {
            message = Component.text("Le joueur ", NamedTextColor.AQUA)
                    .append(Component.text(target.getName(), NamedTextColor.WHITE))
                    .append(Component.text(" a ", NamedTextColor.AQUA))
                    .append(Component.text(points, NamedTextColor.WHITE))
                    .append(Component.text(" point(s) d'hiver ❄", NamedTextColor.AQUA));
        }

        sendMessageWithPrefix(sender, message);
    }

}

