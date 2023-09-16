package fr.cel.essentials.commands.utils;

import org.bukkit.entity.Player;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class DiscordCommand extends AbstractCommand {

    public DiscordCommand(Essentials main) {
        super(main, "discord");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        sendMessageWithPrefix(player, "Le lien Discord est : https://discord.gg/vFjPYC4Mj8");
    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}

}