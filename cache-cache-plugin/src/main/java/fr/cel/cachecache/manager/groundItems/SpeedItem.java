package fr.cel.cachecache.manager.groundItems;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.manager.GroundItem;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SpeedItem extends GroundItem {

    private static final List<String> lores = List.of("Cet objet vous permet d'avoir vous et vos alliés vitesse II pendant quelques secondes.");

    public SpeedItem() {
        super("speedItem", Material.SUGAR, "Vitesse", lores, "cc_speed");
    }

    @Override
    public void onInteract(Player player, CCArena arena) {
        List<Entity> nearbyEntities = player.getNearbyEntities(12, 12, 12);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*5, 1, false, false));
        for (Entity entityNear : nearbyEntities){
            if (entityNear instanceof Player pl) {
                if (arena.getHiders().contains(player.getUniqueId()) && arena.getHiders().contains(pl.getUniqueId())) {
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*3, 1, false, false));
                }
                else if (arena.getSeekers().contains(player.getUniqueId()) && arena.getSeekers().contains(pl.getUniqueId())) {
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*3, 1, false, false));
                }
            }
        }
        super.onInteract(player, arena);
    }
    
}