package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.manager.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TopCommand extends AbstractCommand {

    public TopCommand() {
        super("essentials:top", true, true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        Player target;

        if (!(sender instanceof Player player)) {
            if (args.length != 1) {
                sendMessageWithPrefix(sender, Component.text("Usage: /nv <player>"));
                return;
            }

            Player t = Bukkit.getPlayerExact(args[0]);
            if (isPlayerOnline(t, sender)) target = t;
            else return;
        } else {
            target = player;
        }

        int x = target.getLocation().getBlockX();
        int z = target.getLocation().getBlockZ();

        target.teleport(new Location(target.getWorld(), x, target.getWorld().getHighestBlockYAt(x, z) + 1, z));

        if (target != sender) {
            sendMessageWithPrefix(sender, Component.text(target.getName() + " a été téléporté(e) à la surface."));
        } else {
            sendMessageWithPrefix(sender, Component.text("Tu as été téléporté(e) à la surface."));
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] strings) {
        return null;
    }

}