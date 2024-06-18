package fr.cel.cachecache.manager.groundItems;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.manager.GroundItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SpeedItem extends GroundItem {

    private static final List<String> lores = List.of("Cet objet vous permet d'avoir vitesse II pendant 5 secondes.");

    public SpeedItem() {
        super("speedItem", Material.SUGAR, "Vitesse", lores, 1);
    }

    @Override
    public void onInteract(Player player, CCArena arena) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false, true));
        super.onInteract(player, arena);
    }
    
}