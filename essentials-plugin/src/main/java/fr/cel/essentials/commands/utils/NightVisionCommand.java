package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.manager.command.AbstractCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NightVisionCommand extends AbstractCommand {

    // TODO faire en sorte que ça marche côté console
    public NightVisionCommand() {
        super("nv", false, true);
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

        if (target.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            target.removePotionEffect(PotionEffectType.NIGHT_VISION);

            if (sender != target) {
                sendMessageWithPrefix(sender, Component.text(target.getName() + " n'a plus l'effet Vision Nocturne."));
            } else {
                sendMessageWithPrefix(sender, Component.text("Tu n'as plus l'effet Vision Nocturne."));
            }
        } else {
            target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, false, false));

            if (sender != target) {
                sendMessageWithPrefix(sender, Component.text(target.getName() + " a maintenant l'effet Vision Nocturne."));
            } else {
                sendMessageWithPrefix(sender, Component.text("Tu as maintenant l'effet Vision Nocturne."));
            }
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] strings) {
        return null;
    }

}