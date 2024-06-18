package fr.cel.essentials.commands.other;

import fr.cel.essentials.Essentials;
import fr.cel.gameapi.command.AbstractCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GodCommand extends AbstractCommand {

    @Getter private static final List<UUID> playersInGod = new ArrayList<>();

    public GodCommand() {
        super("essentials:god", true, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        
        if (args.length == 0) {
            if (playersInGod.contains(player.getUniqueId())) {
                playersInGod.remove(player.getUniqueId());
                sendMessageWithPrefix(player, "Vous n'êtes plus en mode invulnérable.");
            } else {
                playersInGod.add(player.getUniqueId());
                sendMessageWithPrefix(player, "Vous êtes en mode invulnérable.");
            }
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (isPlayerOnline(target, player)) {
                if (playersInGod.contains(target.getUniqueId())) {
                    playersInGod.remove(target.getUniqueId());
                    sendMessageWithPrefix(target, "Vous n'êtes plus en mode invulnérable.");
                    sendMessageWithPrefix(player, "Vous avez enlevé le mode invulnérable de " + target.getName() + ".");
                } else {
                    playersInGod.add(player.getUniqueId());
                    sendMessageWithPrefix(target, "Vous avez été mis(e) en mode invulnérable.");
                    sendMessageWithPrefix(player, "Vous avez mis en mode invulnérable " + target.getName() + ".");
                }
            }

        }

    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

}
