package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.manager.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DiscordCommand extends AbstractCommand {

    public DiscordCommand() {
        super("essentials:discord", false, false);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            sendMessageWithPrefix(sender, Component.text("Le lien Discord est : ").append(Component.text("https://discord.gg/vFjPYC4Mj8").decorate(TextDecoration.BOLD)));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (!isPlayerOnline(target, sender)) return;

        if (sender instanceof Player player && !player.isOp()) {
            sendMessageWithPrefix(sender, Component.text("Tu n'as pas la permission d'utiliser cette commande..."));
            return;
        }

        sendMessageWithPrefix(target, Component.text("Le lien Discord est : ").append(Component.text("https://discord.gg/vFjPYC4Mj8").decorate(TextDecoration.BOLD)));
    }


    @Override
    protected List<String> onTabComplete(Player player, String[] strings) {
        return null;
    }

}