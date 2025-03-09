package fr.cel.cachecache.manager.groundItems;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.manager.GroundItem;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class InvisibilityItem extends GroundItem {

    private static final List<String> lores = List.of("Cet objet vous permet de vous rendre invisible vous et vos alliés proches pendant quelques secondes.");

    public InvisibilityItem() {
        super("invisibilityItem", Material.GOLDEN_CARROT, "Invisibilité", lores, "cc_invisibility");
    }

    /**
     * @author Deadsky
     */
    @Override
    public void onInteract(Player player, CCArena arena) {
        List<Entity> nearbyEntities = player.getNearbyEntities(12, 12, 12);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*7, 1, false, false));
        for (Entity entityNear : nearbyEntities){
            if (entityNear instanceof Player pl) {
                if (arena.getHiders().contains(player.getUniqueId()) && arena.getHiders().contains(pl.getUniqueId())) {
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*4, 1, false, false));
                }
                else if (arena.getSeekers().contains(player.getUniqueId()) && arena.getSeekers().contains(pl.getUniqueId())) {
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*4, 1, false, false));
                }
            }
        }
        super.onInteract(player, arena);
    }

}