package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DiscordCommand extends AbstractCommand {

    // TODO faire en sorte que ça marche côté console (l'envoyer à un joueur)
    public DiscordCommand() {
        super("essentials:discord", false, false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        sendMessageWithPrefix(sender, "Le lien Discord est : https://discord.gg/vFjPYC4Mj8");
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] strings) {
        return null;
    }

}