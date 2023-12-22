package fr.cel.cachecache.manager.items;

import fr.cel.cachecache.manager.GroundItem;
import fr.cel.cachecache.manager.arena.CCArena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class InvisibilityItem extends GroundItem {

    private static List<String> lores = Arrays.asList("Cet objet vous permet de vous rendre invisible aux autres joueurs.");

    public InvisibilityItem() {
        super("invisibilityItem", Material.FERMENTED_SPIDER_EYE, "Invisibilité", lores, 1);
    }

    @Override
    public void onInteract(Player player, CCArena arena) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1));
        super.onInteract(player, arena);
    }

}
