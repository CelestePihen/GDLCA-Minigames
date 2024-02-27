package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.command.CommandSender;

public class DiscordCommand extends AbstractCommand {

    public DiscordCommand() {
        super("essentials:discord", false, false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        sendMessageWithPrefix(sender, "Le lien Discord est : https://discord.gg/vFjPYC4Mj8");
    }

}