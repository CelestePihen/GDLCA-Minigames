package fr.cel.gameapi.command;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.manager.PlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CoinsCommand extends AbstractCommand {

    private final PlayerManager playerManager;

    public CoinsCommand(PlayerManager playerManager) {
        super("gameapi:coins", false, true);
        this.playerManager = playerManager;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelp(sender);
        }

        else if (args[0].equalsIgnoreCase("get")) {
            if (args.length == 1) {
                if (sender instanceof Player player) {
                    sendCoinsMessage(player, player);
                } else {
                    sender.sendMessage(GameAPI.getPrefix().append(Component.text("Vous n'êtes pas un joueur...")));
                }
                return;
            }

            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (isPlayerOnline(target, sender)) {
                    sendCoinsMessage(sender, target);
                }
                return;
            }

            sendHelp(sender);
        }

        else if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);

                if (isPlayerOnline(target, sender)) {
                    double amount;
                    try {
                        amount = Double.parseDouble(args[2]);
                    } catch (NumberFormatException e) {
                        sendHelp(sender);
                        return;
                    }
                    playerManager.getPlayerData(target).addCoins(amount);
                    sendMessageWithPrefix(sender, Component.text("Vous avez donné " + amount + " pièce(s) à " + target.getName()));
                }
                return;
            }
            sendHelp(sender);
        }

        else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);

                if (isPlayerOnline(target, sender)) {
                    double amount;
                    try {
                        amount = Double.parseDouble(args[2]);
                    } catch (NumberFormatException e) {
                        sendHelp(sender);
                        return;
                    }
                    playerManager.getPlayerData(target).removeCoins(amount);
                    sendMessageWithPrefix(sender, Component.text("Vous avez retiré " + amount + " pièce(s) à " + target.getName()));
                }
                return;
            }
            sendHelp(sender);
        }

    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return List.of("add", "remove", "get");
        }

        if (args.length == 2) {
            return null;
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                return IntStream.range(1, 10).mapToObj(Integer::toString).collect(Collectors.toList());
            }
        }

        return null;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(Component.text("[Aide pour les commandes du sytème de pièces]", NamedTextColor.GOLD));
        sender.sendMessage("/coins add <pseudo> <montant> : Ajouter des pièces à un joueur");
        sender.sendMessage("/coins remove <pseudo> <montant> : Retirer des pièces à un joueur");
        sender.sendMessage("/coins get <pseudo> : Obtenir le nombre de pièces d'un joueur");
    }

    private void sendCoinsMessage(CommandSender player, Player target) {
        double coins = playerManager.getPlayerData(target).getCoins();
        if (coins < 2) {
            sendMessageWithPrefix(player, Component.text("Le joueur " + target.getName() + " a " + coins + " pièce"));
        } else {
            sendMessageWithPrefix(player, Component.text("Le joueur " + target.getName() + " a " + coins + " pièces"));
        }
    }

}