package fr.cel.cachecache.manager.items;

import java.util.Arrays;
import java.util.List;

import fr.cel.cachecache.manager.arena.CCArena;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.cel.cachecache.manager.GroundItem;

public class BlindnessItem extends GroundItem {

    private static List<String> lores = Arrays.asList("Cet objet vous permet de rendre aveugle tous les joueurs de la partie sauf vous pendant 5 secondes.");

    public BlindnessItem() {
        super("blindnessItem", Material.FERMENTED_SPIDER_EYE, "Aveuglement", lores, 1);
    }

    @Override
    public void onInteract(Player player, CCArena arena) {
        arena.getPlayers().forEach(uuid -> {
            Player pl = Bukkit.getPlayer(uuid);
            if (pl == null) return;
            if (pl == player || pl.getGameMode() == GameMode.SPECTATOR) return;
            pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1, false, false, true));

            super.onInteract(player, arena);
        });
    }
    
}