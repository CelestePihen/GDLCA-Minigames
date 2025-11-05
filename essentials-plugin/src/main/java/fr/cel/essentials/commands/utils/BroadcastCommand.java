package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.manager.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BroadcastCommand extends AbstractCommand {

    public BroadcastCommand() {
        super("essentials:broadcast", false, true);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length >= 1) {
            String message = String.join(" ", args);
            Bukkit.broadcast(Component.text("[Broadcast] ", NamedTextColor.RED).append(Component.text(message)));
        } else {
            sendMessageWithPrefix(sender, Component.text("Usage: /broadcast <message>"));
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] strings) {
        return null;
    }

}
