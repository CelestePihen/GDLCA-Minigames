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
        player.sendMessage(main.getPrefix() + "Le lien Discord est : https://discord.gg/vFjPYC4Mj8");
    }
    
}