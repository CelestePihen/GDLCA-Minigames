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
            player.sendMessage(main.getPrefix() + "Tu n'as plus l'effet Vision Nocturne.");
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, false, false));
            player.sendMessage(main.getPrefix() + "Tu as maintenant l'effet Vision Nocturne.");
        }
    }
    
}
