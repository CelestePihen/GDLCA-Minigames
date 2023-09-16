package fr.cel.essentials.commands.utils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.cel.essentials.Essentials;
import fr.cel.essentials.commands.AbstractCommand;

public class NightVisionCommand extends AbstractCommand {

    public NightVisionCommand(Essentials main) {
        super(main, "nv");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            sendMessageWithPrefix(player, "Tu n'as plus l'effet Vision Nocturne.");
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, false, false));
            sendMessageWithPrefix(player, "Tu as maintenant l'effet Vision Nocturne.");
        }
    }

    @Override
    protected void onTabComplete(Player player, String label, String[] args) {}

}
