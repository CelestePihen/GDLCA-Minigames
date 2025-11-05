package fr.cel.essentials.commands.general;

import fr.cel.gameapi.manager.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FeedCommand extends AbstractCommand {

    public FeedCommand() {
        super("essentials:feed", false, true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            if (args.length == 0) {
                sendMessageWithPrefix(sender, Component.text("Usage: /feed <player>"));
                return;
            }

            Player target = Bukkit.getPlayerExact(args[0]);

            if (isPlayerOnline(target, sender)) {
                target.setFoodLevel(20);
                sendMessageWithPrefix(sender, Component.text("Vous avez rassasié " + target.getName()));
            }

            return;
        }

        if (args.length == 0) {
            player.setFoodLevel(20);
            sendMessageWithPrefix(player, Component.text("Vous vous êtes rassasié(e)."));
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (isPlayerOnline(target, sender)) {
                target.setFoodLevel(20);
                sendMessageWithPrefix(sender, Component.text("Vous avez rassasié " + target.getName()));
            }
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return List.of();
    }

}