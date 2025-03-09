package fr.cel.cachecache.manager.groundItems;

import fr.cel.cachecache.arena.CCArena;
import fr.cel.cachecache.manager.GroundItem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;

public class BlindnessItem extends GroundItem {

    private static final List<String> lores = List.of("Cet objet vous permet de rendre aveugle tous les joueurs de la partie sauf vous pendant 5 secondes.");

    public BlindnessItem() {
        super("blindnessItem", Material.FERMENTED_SPIDER_EYE, "Aveuglement", lores, "cc_blindness");
    }

    @Override
    public void onInteract(Player player, CCArena arena) {
        for (UUID uuid : arena.getPlayers()) {
            Player pl = Bukkit.getPlayer(uuid);
            if (pl == null) continue;
            if (pl == player || pl.getGameMode() == GameMode.SPECTATOR) continue;
            pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1, false, false, true));

            super.onInteract(player, arena);
        }
    }
    
}