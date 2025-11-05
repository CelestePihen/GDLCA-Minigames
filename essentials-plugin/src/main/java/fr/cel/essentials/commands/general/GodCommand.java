package fr.cel.essentials.commands.general;

import fr.cel.gameapi.manager.command.AbstractCommand;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GodCommand extends AbstractCommand {

    @Getter private static final List<UUID> playersInGod = new ArrayList<>();

    public GodCommand() {
        super("essentials:god", true, true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sendMessageWithPrefix(sender, Component.text("Cette commande doit être exécutée par un joueur ou précisez un joueur."));
                return;
            }

            toggleGodMode(player, sender);
            return;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (!isPlayerOnline(target, sender)) return;

            toggleGodMode(target, sender);
            return;
        }

        sendMessageWithPrefix(sender, Component.text("Usage : /god [player]"));
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return List.of();
    }


    /**
     * Toggle the god mode of a player.
     *
     * @param target The player that we modify the mode.
     * @param sender The sender of the command.
     */
    private void toggleGodMode(@NotNull Player target, @NotNull CommandSender sender) {
        UUID uuid = target.getUniqueId();

        boolean enabled = !playersInGod.contains(uuid);

        if (enabled) {
            playersInGod.add(uuid);
            target.setInvulnerable(true);
            sendMessageWithPrefix(sender, Component.text("Vous êtes maintenant en mode invulnérable."));
            if (sender != target) {
                sendMessageWithPrefix(sender, Component.text("Vous avez activé le mode invulnérable pour " + target.getName() + "."));
            }
        } else {
            playersInGod.remove(uuid);
            target.setInvulnerable(false);
            sendMessageWithPrefix(sender, Component.text("Vous n'êtes plus en mode invulnérable."));
            if (sender != target) {
                sendMessageWithPrefix(sender, Component.text("Vous avez désactivé le mode invulnérable de " + target.getName() + "."));
            }
        }
    }

}