package fr.cel.essentials.commands.utils;

import fr.cel.gameapi.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class NightVisionCommand extends AbstractCommand {

    // TODO faire en sorte que ça marche côté console
    public NightVisionCommand() {
        super("nv", true, true);
    }

    @Override
    protected void onExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            sendMessageWithPrefix(player, "Tu n'as plus l'effet Vision Nocturne.");
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, false, false));
            sendMessageWithPrefix(player, "Tu as maintenant l'effet Vision Nocturne.");
        }
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] strings) {
        return null;
    }

}
